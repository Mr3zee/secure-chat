package com.example.secure.chat.domain.repository

import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.domain.db.util.Transactional

interface InviteRepository {
    fun Transactional.createInvite(invite: Invite)
    fun Transactional.getUserInvites(rqUserId: Long): List<Invite>
    fun Transactional.deleteInvite(rq: InviteAcceptRq)
}
