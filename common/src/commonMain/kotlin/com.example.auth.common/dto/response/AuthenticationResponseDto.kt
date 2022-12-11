package com.example.auth.common.dto.response

import com.example.auth.common.dto.request.AuthenticationRequestDto
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializableAuthenticationResponseDto

interface AuthenticationResponseDto<REQUEST_TYPE, RESPONSE_TYPE> : SerializableAuthenticationResponseDto
        where REQUEST_TYPE : AuthenticationRequestDto<REQUEST_TYPE, RESPONSE_TYPE>,
              RESPONSE_TYPE : AuthenticationResponseDto<REQUEST_TYPE, RESPONSE_TYPE>
