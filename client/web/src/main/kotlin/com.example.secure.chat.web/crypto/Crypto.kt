@file:Suppress("unused")

package com.example.secure.chat.web.crypto

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

@JsName("crypto")
private external val proxyCrypto: ProxyCrypto

external class ProxyCrypto {
    val subtle: Crypto
}

@JsName("cryptoSubtle")
val crypto: Crypto = proxyCrypto.subtle

external class Crypto {
    @JsName("generateKey")
    fun generateKeyPair(
        algorithm: PublicKeyAlgorithmParams,
        extractable: Boolean,
        keyUsages: Array<String>,
    ): Promise<CryptoKeyPair>

    fun exportKey(format: String, key: CryptoKey): Promise<dynamic>

    fun importKey(
        format: String,
        keyData: ArrayBuffer,
        algorithm: PublicKeyAlgorithmParams,
        extractable: Boolean,
        keyUsages: Array<String>,
    ): Promise<CryptoKey>

    fun encrypt(algorithm: RsaEncryptParams, key: PublicCryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>

    fun decrypt(algorithm: RsaDecryptParams, key: PrivateCryptoKey, data: ArrayBuffer): Promise<ArrayBuffer>
}

sealed external interface PublicKeyAlgorithmParams

external interface RsaHashedKeyGenParams : PublicKeyAlgorithmParams {
    var name: String
    var modulusLength: Int
    var publicExponent: Uint8Array
    var hash: String
}

external interface RsaEncryptParams {
    var name: String
}

external interface RsaDecryptParams {
    var name: String
}

external interface CryptoKeyPair {
    var publicKey: PublicCryptoKey

    var privateKey: PrivateCryptoKey
}

operator fun CryptoKeyPair.component1(): PublicCryptoKey = publicKey

operator fun CryptoKeyPair.component2(): PrivateCryptoKey = privateKey

sealed external interface CryptoKey {
    val type: String
    val extractable: Boolean
}

external interface PublicCryptoKey : CryptoKey

external interface PrivateCryptoKey : CryptoKey
