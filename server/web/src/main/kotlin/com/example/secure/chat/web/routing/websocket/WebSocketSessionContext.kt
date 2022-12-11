package com.example.secure.chat.web.routing.websocket

import com.example.secure.chat.base.model.user.User
import io.ktor.server.websocket.*

class WebSocketSessionContext (
    serverSession: WebSocketServerSession,
    val currentUser: User,
) : WebSocketServerSession by serverSession
