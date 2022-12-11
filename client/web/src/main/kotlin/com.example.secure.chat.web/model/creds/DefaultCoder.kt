package com.example.secure.chat.web.model.creds

import com.example.secure.chat.web.crypto.*
import org.khronos.webgl.ArrayBuffer

object DefaultCoder : Coder {
    override suspend fun exportPublicRSAKey(key: PublicCryptoKey): ArrayBuffer {
        return crypto.exportPublicRSAKey(key)
    }

    override suspend fun exportPrivateRSAKey(key: PrivateCryptoKey): ArrayBuffer {
        return crypto.exportPrivateRSAKey(key)
    }

    override suspend fun exportPrivateRSAKeyPEM(key: PrivateCryptoKey): String {
        return crypto.exportPrivateRSAKeyPEM(key)
    }

    override suspend fun importRSAPrivateKeyPEM(string: String): PrivateCryptoKey {
        return crypto.importRSAPrivateKeyPEM(string)
    }

    override suspend fun safeImportRSAPrivateKeyPEM(string: String): PrivateCryptoKey? {
        return try {
            crypto.importRSAPrivateKeyPEM(string)
        } catch (e: dynamic) {
            console.warn("Failed to import key", e)
            null
        }
    }

    override suspend fun genRsaKeyPair(): CryptoKeyPair {
        return crypto.genRsaKeyPair()
    }

    override suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: ArrayBuffer): ArrayBuffer? {
        return try {
            crypto.encryptRSA(publicKey, data)
        } catch (e: dynamic) {
            console.warn("Failed to encrypt", e)
            null
        }
    }

    override suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: String): ArrayBuffer? {
        return try {
            crypto.encryptRSA(publicKey, data)
        } catch (e: dynamic) {
            console.warn("Failed to encrypt", e)
            null
        }
    }

    override suspend fun safeDecryptRSA(privateKey: PrivateCryptoKey, data: ArrayBuffer): ArrayBuffer? {
        return try {
            crypto.decryptRSA(privateKey, data)
        } catch (e: dynamic) {
            console.warn("Failed to decrypt", e)
            null
        }
    }
}
