package com.example.secure.chat.web.model.coder

import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import org.khronos.webgl.ArrayBuffer

interface Coder {
    suspend fun exportPublicRSAKey(key: PublicCryptoKey): ArrayBuffer

    suspend fun exportPrivateRSAKey(key: PrivateCryptoKey): ArrayBuffer

    suspend fun exportPrivateRSAKeyPEM(key: PrivateCryptoKey): String

    suspend fun importRSAPrivateKeyPEM(string: String): PrivateCryptoKey

    suspend fun safeImportRSAPrivateKeyPEM(string: String): PrivateCryptoKey?

    suspend fun genRsaKeyPair(): CryptoKeyPair

    suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: ArrayBuffer): ArrayBuffer?

    suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: String): ArrayBuffer?

    suspend fun safeDecryptRSA(privateKey: PrivateCryptoKey, data: ArrayBuffer): ArrayBuffer?
}