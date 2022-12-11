package com.example.auth.common.dto.response

import com.example.auth.common.dto.request.AuthenticationRequestDto

interface AuthenticationResponseDto<REQUEST_TYPE, RESPONSE_TYPE>
        where REQUEST_TYPE : AuthenticationRequestDto<REQUEST_TYPE, RESPONSE_TYPE>,
              RESPONSE_TYPE : AuthenticationResponseDto<REQUEST_TYPE, RESPONSE_TYPE>
