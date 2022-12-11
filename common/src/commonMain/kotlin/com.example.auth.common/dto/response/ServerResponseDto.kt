package com.example.auth.common.dto.response

import com.example.auth.common.dto.request.ClientRequestDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializableServerResponseDto

@Serializable
sealed interface ServerResponseDto<REQUEST_TYPE, RESPONSE_TYPE> : SerializableServerResponseDto
        where RESPONSE_TYPE : ServerResponseDto<REQUEST_TYPE, RESPONSE_TYPE>,
              REQUEST_TYPE : ClientRequestDto<REQUEST_TYPE, RESPONSE_TYPE> {
    val requestId: Long
}
