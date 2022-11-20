package com.example.auth.common.decode

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class UserDecodeRequestDto(
    val encodedData: RawBytesDto,
)
