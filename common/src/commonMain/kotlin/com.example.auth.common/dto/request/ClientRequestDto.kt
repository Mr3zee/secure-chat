package com.example.auth.common.dto.request

import com.example.auth.common.dto.response.ServerResponseDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializableClientRequestDto

@Serializable
sealed interface ClientRequestDto<REQUEST_TYPE, RESPONSE_TYPE> : SerializableClientRequestDto
        where REQUEST_TYPE : ClientRequestDto<REQUEST_TYPE, RESPONSE_TYPE>,
              RESPONSE_TYPE : ServerResponseDto<REQUEST_TYPE, RESPONSE_TYPE> {
    val requestId: Long
}
