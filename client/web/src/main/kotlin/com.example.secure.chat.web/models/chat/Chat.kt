package com.example.secure.chat.web.models.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.auth.common.dto.model.chat.ChatDto

sealed interface Chat {
    val key: MutableState<String?>
    val isLocked: State<Boolean>
    val lastMessage: MutableState<Message?>

    object Local : Chat {
        override val key: MutableState<String?> = mutableStateOf(null)
        override val isLocked: State<Boolean> = mutableStateOf(false)
        override val lastMessage: MutableState<Message?> = mutableStateOf(null)
    }

    data class Global(
        override val isLocked: MutableState<Boolean>,
        override val key: MutableState<String?>,
        val dto: ChatDto,
    ) : Chat {
        override val lastMessage = mutableStateOf<Message?>(null)
    }
}
