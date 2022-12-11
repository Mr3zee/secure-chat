package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.domain.db.tables.ChatTables.Invites
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.domain.repository.InviteRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore

object InviteRepositoryImpl : InviteRepository {

    override fun Transaction.createInvite(invite: Invite) {
        Invites.insertIgnore {
            it[userId] = invite.userId
            it[chatId] = invite.chatId
            it[encodedKey] = invite.encodedKey.byteArray
        }
    }

    override fun Transaction.deleteInvite(rq: InviteAcceptRq) {
        Invites.deleteWhere {
            userId.eq(rq.userId).and(chatId.eq(rq.chatId))
        }
    }
}
