package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.domain.db.tables.ChatTables.Invites
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.repository.InviteRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object InviteRepositoryImpl : InviteRepository {

    override fun Transactional.createInvite(invite: Invite) {
        Invites.insertIgnore {
            it[userId] = invite.userId
            it[chatId] = invite.chatId
            it[encodedKey] = invite.encodedKey.byteArray
        }
    }

    override fun Transactional.getUserInvites(rqUserId: Long): List<Invite> =
        Invites.select {
            Invites.userId.eq(rqUserId)
        }.map { row ->
            Invite(
                row[Invites.userId],
                row[Invites.chatId],
                ByteArrayWrapper(row[Invites.encodedKey]),
            )
        }

    override fun Transactional.deleteInvite(rq: InviteAcceptRq) {
        Invites.deleteWhere {
            userId.eq(rq.user.id).and(chatId.eq(rq.chatId))
        }
    }
}
