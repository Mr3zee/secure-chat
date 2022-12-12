package com.example.secure.chat.web.routing.websocket

import com.example.secure.chat.base.model.user.User
import io.ktor.server.websocket.*

class WebSocketSessionContext(
    val sessionId: Long,
    val currentUser: User,
    serverSession: WebSocketServerSession,
) : WebSocketServerSession by serverSession
