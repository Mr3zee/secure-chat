package com.example.secure.chat.web.controller.impl.converter

import com.example.auth.common.dto.model.byte.Base64BytesDto
import com.example.secure.chat.base.model.wrapper.Base64Bytes


fun toDto(model: Base64Bytes) = Base64BytesDto(
    model.content,
)

fun toModel(dto: Base64BytesDto) = Base64Bytes(
    dto.content,
)
