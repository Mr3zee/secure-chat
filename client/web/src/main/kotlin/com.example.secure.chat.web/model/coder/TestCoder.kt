package com.example.secure.chat.web.model.coder

import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import org.khronos.webgl.ArrayBuffer

object TestCoder : Coder {
    override suspend fun exportPublicRSAKey(key: PublicCryptoKey): ArrayBuffer {
        return DefaultCoder.exportPublicRSAKey(key)
    }

    override suspend fun exportPrivateRSAKey(key: PrivateCryptoKey): ArrayBuffer {
        return DefaultCoder.exportPrivateRSAKey(key)
    }

    override suspend fun exportPrivateRSAKeyPEM(key: PrivateCryptoKey): String {
        return DefaultCoder.exportPrivateRSAKeyPEM(key)
    }

    override suspend fun importRSAPrivateKeyPEM(string: String): PrivateCryptoKey {
        return DefaultCoder.importRSAPrivateKeyPEM(string)
    }

    override suspend fun safeImportRSAPrivateKeyPEM(string: String): PrivateCryptoKey? {
        return DefaultCoder.safeImportRSAPrivateKeyPEM(string)
    }

    override suspend fun genRsaKeyPair(): CryptoKeyPair {
        return DefaultCoder.genRsaKeyPair()
    }

    override suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: ArrayBuffer): ArrayBuffer? {
        return DefaultCoder.safeEncryptRSA(publicKey, data)
    }

    override suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: String): ArrayBuffer? {
        return DefaultCoder.safeEncryptRSA(publicKey, data)
    }

    override suspend fun safeDecryptRSA(privateKey: PrivateCryptoKey, data: ArrayBuffer): ArrayBuffer? {
        return DefaultCoder.safeDecryptRSA(privateKey, data)
    }
}
