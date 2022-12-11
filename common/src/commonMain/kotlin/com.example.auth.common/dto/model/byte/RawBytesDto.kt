package com.example.auth.common.dto.model.byte

import kotlinx.serialization.Serializable

@Serializable
data class RawBytesDto(
    val content: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RawBytesDto

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}
