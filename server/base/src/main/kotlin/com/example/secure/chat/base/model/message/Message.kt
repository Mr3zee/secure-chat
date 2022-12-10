package com.example.secure.chat.base.model.message

import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper

data class Message(
    val id: Long,
    val text: ByteArrayWrapper,
)
