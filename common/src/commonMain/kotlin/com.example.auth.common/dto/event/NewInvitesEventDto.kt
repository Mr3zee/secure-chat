package com.example.auth.common.dto.event

import com.example.auth.common.dto.model.invite.InviteDto
import kotlinx.serialization.Serializable

@Serializable
data class NewInvitesEventDto(
    val invites: List<InviteDto>,
) : ServerEventDto
