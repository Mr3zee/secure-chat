package com.example.secure.chat.web.model.coder

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

    override suspend fun importRSAPrivateKey(data: ArrayBuffer): PrivateCryptoKey {
        return crypto.importRSAPrivateKey(data)
    }

    override suspend fun importRSAPublicKey(data: ArrayBuffer): PublicCryptoKey {
        return crypto.importRSAPublicKey(data)
    }

    override suspend fun safeImportRSAPrivateKeyPEM(string: String): PrivateCryptoKey? {
        return safeImport {
            importRSAPrivateKeyPEM(string)
        }
    }

    override suspend fun safeImportRSAPrivateKey(data: ArrayBuffer): PrivateCryptoKey? {
        return safeImport {
            importRSAPrivateKey(data)
        }
    }

    override suspend fun safeImportRSAPublicKey(data: ArrayBuffer): PublicCryptoKey? {
        return safeImport {
            importRSAPublicKey(data)
        }
    }

    private suspend fun <T> safeImport(body: suspend () -> T): T? {
        return try {
            body()
        } catch (e: dynamic) {
            console.warn("Failed to import key")
            console.warn(e)
            null
        }
    }

    override suspend fun genRsaKeyPair(): CryptoKeyPair {
        return crypto.genRsaKeyPair()
    }

    override suspend fun encryptRSA(publicKey: PublicCryptoKey, data: ArrayBuffer): ArrayBuffer? {
        return crypto.encryptRSA(publicKey, data)
    }

    override suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: ArrayBuffer): ArrayBuffer? {
        return try {
            encryptRSA(publicKey, data)
        } catch (e: dynamic) {
            console.warn("Failed to encrypt")
            console.warn(e)
            null
        }
    }

    override suspend fun safeEncryptRSA(publicKey: PublicCryptoKey, data: String): ArrayBuffer? {
        return try {
            crypto.encryptRSA(publicKey, data)
        } catch (e: dynamic) {
            console.warn("Failed to encrypt")
            console.warn(e)
            null
        }
    }

    override suspend fun safeDecryptRSA(privateKey: PrivateCryptoKey, data: ArrayBuffer): ArrayBuffer? {
        return try {
            crypto.decryptRSA(privateKey, data)
        } catch (e: dynamic) {
            console.warn("Failed to decrypt")
            console.warn(e)
            null
        }
    }
}
