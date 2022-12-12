package com.example.secure.chat.core.registry

interface Registry<T, ENTITY_ID : Comparable<ENTITY_ID>> {

    fun startPolling()

    suspend fun subscribe(
        entityId: ENTITY_ID,
        sessionId: Long,
        action: suspend (List<T>) -> Boolean,
    )
}
