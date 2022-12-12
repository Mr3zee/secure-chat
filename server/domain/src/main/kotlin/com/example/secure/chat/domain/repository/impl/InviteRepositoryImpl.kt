package com.example.secure.chat.domain.repository.impl

import com.example.secure.chat.domain.db.tables.ChatTables.Invites
import com.example.secure.chat.base.model.invite.Invite
import com.example.secure.chat.base.model.invite.InviteAcceptRq
import com.example.secure.chat.base.model.invite.InviteCreateRq
import com.example.secure.chat.base.model.wrapper.Base64Bytes
import com.example.secure.chat.domain.db.util.Transactional
import com.example.secure.chat.domain.repository.InviteRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object InviteRepositoryImpl : InviteRepository {

    override fun Transactional.createInvite(invite: InviteCreateRq) {
        Invites.insertIgnore {
            it[userId] = invite.userId
            it[chatId] = invite.chatId
            it[encodedKey] = invite.encodedKey.content
        }
    }

    override fun Transactional.getUserInvites(rqUserId: Long): List<Invite> =
        Invites.select {
            Invites.userId.eq(rqUserId)
        }.map { row ->
            Invite(
                row[Invites.id].value,
                row[Invites.userId],
                row[Invites.chatId],
                Base64Bytes(row[Invites.encodedKey]),
                row[Invites.createdTs],
            )
        }

    override fun Transactional.deleteInvite(rq: InviteAcceptRq) {
        Invites.deleteWhere {
            userId.eq(rq.user.id).and(chatId.eq(rq.chatId))
        }
    }

    override fun Transactional.getNewInvites(idGt: Long, limit: Int): List<Invite> =
        Invites.select {
            Invites.id.greater(idGt)
        }.orderBy(
            Invites.id,
            SortOrder.ASC,
        ).limit(limit).map { row ->
            Invite(
                row[Invites.id].value,
                row[Invites.userId],
                row[Invites.chatId],
                Base64Bytes(row[Invites.encodedKey]),
                row[Invites.createdTs],
            )
        }

    override fun Transactional.getLastInviteId(): Long =
        Invites.slice(Invites.id.max())
            .selectAll()
            .singleOrNull()?.get(Invites.id.max()) ?: 0L
}
