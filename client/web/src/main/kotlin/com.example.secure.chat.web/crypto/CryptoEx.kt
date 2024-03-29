@file:Suppress("unused")

package com.example.secure.chat.web.crypto

import com.example.secure.chat.web.utils.*
import io.ktor.utils.io.core.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.js.jso
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

private const val PRIVATE_PEM_BEGIN = "-----BEGIN PRIVATE KEY-----"
private const val PRIVATE_PEM_END = "-----END PRIVATE KEY-----"

private const val PUBLIC_PEM_BEGIN = "-----BEGIN PUBLIC KEY-----"
private const val PUBLIC_PEM_END = "-----END PUBLIC KEY-----"

private const val PKCS8_FORMAT = "pkcs8"
private const val SPKI_FORMAT = "spki"

private const val RSA_OAEP = "RSA-OAEP"
private const val SHA_512 = "SHA-512"

private const val ENCRYPT_KEY_USAGE = "encrypt"
private const val DECRYPT_KEY_USAGE = "decrypt"

private const val CHUNK_SIZE = 256
private const val ENCRYPTED_CHUNK_SIZE = 512 // SHA-512

val ImportRsaParams = jso<RsaHashedKeyGenParams> {
    name = RSA_OAEP
    hash = SHA_512
}

val GenRsaParams = jso<RsaHashedKeyGenParams> {
    name = RSA_OAEP
    modulusLength = 4096
    publicExponent = Uint8Array(arrayOf(1, 0, 1))
    hash = SHA_512
}


suspend fun Crypto.exportPublicRSAKey(key: PublicCryptoKey): ArrayBuffer {
    return exportKey(SPKI_FORMAT, key).unsafeCast<Promise<ArrayBuffer>>().await()
}

suspend fun Crypto.exportPublicRSAKeyPEM(key: PublicCryptoKey): String {
    return exportPublicRSAKey(key).let {
        val strKey = it.asString()
        val base64Key = window.btoa(strKey)
        "$PUBLIC_PEM_BEGIN\n$base64Key\n$PUBLIC_PEM_END"
    }
}

suspend fun Crypto.exportPrivateRSAKey(key: PrivateCryptoKey): ArrayBuffer {
    return exportKey(PKCS8_FORMAT, key).unsafeCast<Promise<ArrayBuffer>>().await()
}

suspend fun Crypto.exportPrivateRSAKeyPEM(key: PrivateCryptoKey): String {
    return exportPrivateRSAKey(key).let {
        val strKey = it.asString()
        val base64Key = window.btoa(strKey)
        "$PRIVATE_PEM_BEGIN\n$base64Key\n$PRIVATE_PEM_END"
    }
}

suspend fun Crypto.importRSAPrivateKeyPEM(string: String): PrivateCryptoKey {
    val data = string.trim().removePrefix(PRIVATE_PEM_BEGIN).removeSuffix(PRIVATE_PEM_END).trim()
    val raw = window.atob(data)
    val binaryDer = raw.toArrayBuffer()
    return importRSAPrivateKey(binaryDer)
}

suspend fun Crypto.importRSAPrivateKey(data: ArrayBuffer): PrivateCryptoKey {
    return importKey(
        format = PKCS8_FORMAT,
        keyData = data,
        algorithm = ImportRsaParams,
        extractable = true,
        keyUsages = arrayOf(DECRYPT_KEY_USAGE)
    ).await().unsafeCast<PrivateCryptoKey>()
}

suspend fun Crypto.importRSAPublicKey(data: ArrayBuffer): PublicCryptoKey {
    return importKey(
        format = SPKI_FORMAT,
        keyData = data,
        algorithm = ImportRsaParams,
        extractable = true,
        keyUsages = arrayOf(ENCRYPT_KEY_USAGE)
    ).await().unsafeCast<PublicCryptoKey>()
}

suspend fun Crypto.genRsaKeyPair(): CryptoKeyPair {
    return generateKeyPair(
        algorithm = GenRsaParams,
        extractable = true,
        keyUsages = arrayOf(ENCRYPT_KEY_USAGE, DECRYPT_KEY_USAGE)
    ).await()
}

suspend fun Crypto.simpleEncryptRSA(publicKey: PublicCryptoKey, data: ArrayBuffer): ArrayBuffer {
    return encrypt(jso { name = RSA_OAEP }, publicKey, data).await()
}

suspend fun Crypto.simpleEncryptRSA(publicKey: PublicCryptoKey, data: String): ArrayBuffer {
    return simpleEncryptRSA(publicKey, data.toArrayBuffer())
}

suspend fun Crypto.encryptRSA(publicKey: PublicCryptoKey, data: String): ArrayBuffer {
    return encryptRSA(publicKey, data.toArrayBuffer())
}

suspend fun Crypto.encryptRSA(publicKey: PublicCryptoKey, data: ArrayBuffer): ArrayBuffer {
    return data.chunked(CHUNK_SIZE).map {
        simpleEncryptRSA(publicKey, it).toByteArray()
    }.concat().toUint8Array().buffer
}

suspend fun Crypto.simpleDecryptRSA(privateKey: PrivateCryptoKey, data: ArrayBuffer): ArrayBuffer {
    return decrypt(jso { name = RSA_OAEP }, privateKey, data).await()
}

suspend fun Crypto.decryptRSA(privateKey: PrivateCryptoKey, data: ArrayBuffer): ArrayBuffer {
    return data.chunked(ENCRYPTED_CHUNK_SIZE).map {
        simpleDecryptRSA(privateKey, it).toByteArray()
    }.concat().toUint8Array().buffer
}
