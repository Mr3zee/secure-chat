package com.example.secure.chat.core.registry

import com.example.secure.chat.base.model.invite.Invite

interface NewInvitesRegistry : Registry<Invite, Long> {

    override suspend fun subscribe(
        entityId: Long,
        sessionId: Long,
        action: suspend (List<Invite>) -> Boolean,
    )
}
