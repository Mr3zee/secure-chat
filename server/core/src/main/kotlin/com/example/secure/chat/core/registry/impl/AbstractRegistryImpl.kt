package com.example.secure.chat.core.registry.impl

import com.example.secure.chat.core.registry.Registry
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.db.util.tx
import kotlinx.coroutines.*
import mu.KLogger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

abstract class AbstractRegistryImpl<
        T,
        EVENT_ID : Comparable<EVENT_ID>,
        ENTITY_ID : Comparable<ENTITY_ID>,
        > : Registry<T, ENTITY_ID> {

    abstract val logger: KLogger

    abstract fun T.eventId(): EVENT_ID
    abstract fun T.entityId(): ENTITY_ID
    abstract suspend fun Transactional.loadLastEventId(): EVENT_ID
    abstract suspend fun Transactional.loadNewEvents(lastEventId: EVENT_ID): List<T>

    protected open val sessionIdToAction: ConcurrentMap<Long, suspend (List<T>) -> Boolean> = ConcurrentHashMap()
    protected open val entityIdToSessionIds: ConcurrentMap<ENTITY_ID, ConcurrentMap<Long, Unit>> = ConcurrentHashMap()

    override fun startPolling() {
        thread(start = true) {
            runBlocking {
                delay(5.seconds)
                var lastEventId = tx {
                    loadLastEventId()
                }
                logger.debug { "Got lastEventId = $lastEventId" }
                while (true) {
                    delay(5.seconds)
                    lastEventId = iteration(lastEventId)
                }
            }
        }
    }

    protected open suspend fun iteration(lastEventId: EVENT_ID): EVENT_ID = try {
        val new = tx {
            loadNewEvents(lastEventId)
        }
        logger.debug { "Loaded ${new.size} new entities" }

        try {
            val expiredSessions = process(new)
            unsubscribe(expiredSessions)
            new.maxOfOrNull { it.eventId() } ?: lastEventId
        } catch (e: Exception) {
            logger.error(e) { "Failed to process loaded entities" }
            lastEventId
        }
    } catch (e: Exception) {
        logger.error(e) { "Failed to execute processing cycle" }
        lastEventId
    }

    protected open suspend fun process(events: List<T>): Set<Long> {
        val sessionToEvents = buildMap<Long, MutableList<T>> {
            events.forEach { event ->
                val entityId = event.entityId()
                val sessionIds = entityIdToSessionIds[entityId]?.keys ?: return@forEach
                sessionIds.forEach { sessionId ->
                    getOrPut(sessionId, ::mutableListOf) += event
                }
            }
        }
        return coroutineScope {
            sessionToEvents.map { (sessionId, events) ->
                val action = sessionIdToAction.getValue(sessionId)
                async {
                    sessionId to action.invoke(events.toList())
                }
            }.awaitAll().filterNot { it.second }.map { it.first }.toSet()
        }
    }

    protected open fun unsubscribe(sessionIds: Set<Long>) {
        entityIdToSessionIds.keys.forEach { entityId ->
            entityIdToSessionIds.compute(entityId) { _, sessions ->
                sessions?.apply {
                    sessionIds.forEach(this::remove)
                }?.takeIf { it.isNotEmpty() }
            }
        }
        sessionIds.forEach(sessionIdToAction::remove)
    }

    override suspend fun subscribe(
        entityId: ENTITY_ID,
        sessionId: Long,
        action: suspend (
            List<T>,
        ) -> Boolean,
    ) {
        sessionIdToAction[sessionId] = action
        entityIdToSessionIds.compute(entityId) { _, sessions ->
            (sessions ?: ConcurrentHashMap()).apply {
                put(sessionId, Unit)
            }
        }
    }
}
