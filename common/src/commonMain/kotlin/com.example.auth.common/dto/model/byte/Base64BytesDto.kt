package com.example.auth.common.dto.model.byte

import kotlinx.serialization.Serializable

@Serializable
data class Base64BytesDto(
    val content: String,
)
