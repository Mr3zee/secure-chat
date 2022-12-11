package com.example.secure.chat.web.model.chat

import com.example.secure.chat.web.compose.MutableProperty
import com.example.secure.chat.web.compose.Property
import com.example.secure.chat.web.compose.mutableProperty

sealed interface Chat {
    val isLocked: Property<Boolean>
    val lastMessage: MutableProperty<Message?>

    object Local : Chat {
        override val isLocked: Property<Boolean> = mutableProperty(false)
        override val lastMessage: MutableProperty<Message?> = mutableProperty(null)
    }

    data class Global(val id: Long, val name: String) : Chat {
        override val isLocked: MutableProperty<Boolean> = mutableProperty(true)

        override val lastMessage = mutableProperty<Message?>(null)
    }
}
