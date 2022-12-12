package com.example.auth.common.dto.response

import kotlinx.serialization.Serializable

@Serializable
sealed interface ServerEventDto : SerializableServerResponseDto
