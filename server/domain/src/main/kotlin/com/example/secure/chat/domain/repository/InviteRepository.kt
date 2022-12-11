package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteDeleteRq
import org.jetbrains.exposed.sql.Transaction

interface InviteRepository {
    fun Transaction.createInvite(invite: Invite)
    fun Transaction.deleteInvite(rq: InviteDeleteRq)
}
