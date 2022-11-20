package com.example.auth.common.encode

import com.example.auth.common.byte.RawBytesDto
import kotlinx.serialization.Serializable

@Serializable
data class EncodeRqDto(
    val encodedData: RawBytesDto,
)
