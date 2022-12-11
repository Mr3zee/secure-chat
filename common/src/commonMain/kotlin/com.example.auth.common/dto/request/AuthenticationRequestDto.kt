package com.example.auth.common.dto.request

import com.example.auth.common.dto.response.AuthenticationResponseDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializableAuthenticationRequestDto

sealed interface AuthenticationRequestDto<REQUEST_TYPE, RESPONSE_TYPE> : SerializableAuthenticationRequestDto
        where REQUEST_TYPE : AuthenticationRequestDto<REQUEST_TYPE, RESPONSE_TYPE>,
              RESPONSE_TYPE : AuthenticationResponseDto<REQUEST_TYPE, RESPONSE_TYPE>
