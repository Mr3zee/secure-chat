package com.example.secure.chat.web.controller.impl.converter

import com.example.auth.common.dto.model.byte.RawBytesDto
import com.example.secure.chat.base.model.wrapper.ByteArrayWrapper


fun toDto(model: ByteArrayWrapper) = RawBytesDto(
    model.byteArray,
)

fun toModel(dto: RawBytesDto) = ByteArrayWrapper(
    dto.content,
)
