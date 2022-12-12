@file:Suppress("unused")

package com.example.secure.chat.web.utils

import com.example.auth.common.dto.model.byte.Base64BytesDto
import io.ktor.utils.io.core.*
import kotlinx.browser.window
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set

external fun decodeURIComponent(encodedURI: String): String

external fun encodeURIComponent(encodedURI: String): String

inline fun Uint8Array.asString() = decoder.decode(this)

fun Uint8Array.toByteArray() = asDynamic().unsafeCast<ByteArray>()

fun ByteArray.toUint8Array() = asDynamic().unsafeCast<Uint8Array>()

inline fun ArrayBuffer.asString(): String {
    return js("String.fromCharCode").apply(null, Uint8Array(this)) as String
}

inline fun ArrayBuffer.toByteArray(): ByteArray {
    return Uint8Array(this).toByteArray()
}

fun String.decodeBase64(): String {
    return window.atob(this)
}

fun String.encodeBase64(): String {
    return window.btoa(this)
}

inline fun ArrayBuffer.toBase64Bytes(): Base64BytesDto {
    return Base64BytesDto(asString().encodeBase64())
}

fun Base64BytesDto.toArrayBuffer(): ArrayBuffer {
    return content.decodeBase64().toArrayBuffer()
}


fun String.toArrayBuffer(): ArrayBuffer {
    val buf = ArrayBuffer(length)
    val bufView = Uint8Array(buf)
    for (i in indices) {
        bufView[i] = get(i).code.toByte()
    }
    return buf
}