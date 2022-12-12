package com.example.secure.chat.core.registry.impl

import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.core.registry.NewInvitesRegistry
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.repository.InviteRepository
import mu.KLogger
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NewInvitesRegistryImpl : AbstractRegistryImpl<Invite, Long, Long>(), NewInvitesRegistry, KoinComponent {

    private val inviteRepository by inject<InviteRepository>()

    override val logger: KLogger = KotlinLogging.logger { }

    override fun Invite.entityId(): Long = userId
    override fun Invite.eventId(): Long = id

    override suspend fun Transactional.loadLastEventId(): Long =
        with(inviteRepository) { getLastInviteId() }

    override suspend fun Transactional.loadNewEvents(lastEventId: Long): List<Invite> =
        with(inviteRepository) { getNewInvites(lastEventId, 100) }
}
