package com.example.secure.chat.web.model.creds

import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.crypto.PublicCryptoKey
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.utils.downloadFile
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Credentials {
    val login = mutableProperty<String?>(null)
    val keyPair = mutableProperty<CryptoKeyPair?>(null)
    val chatKeys = mutableMapOf<Long, CryptoKeyPair>()
    val chatsLonePublicKeys = mutableMapOf<Long, PublicCryptoKey>()

    fun loginContext(coder: Coder): ApiContext = ApiContext(
        username = login.value ?: error("Expected username"),
        privateCryptoKey = keyPair.value?.privateKey ?: error("Expected privateKey"),
        publicCryptoKey = keyPair.value?.publicKey,
        coder = coder,
        chatKeys = chatKeys
    )

    fun clear() {
        keyPair.value = null
        login.value = null
        chatKeys.clear()
        chatsLonePublicKeys.clear()
    }

    suspend fun dumpFile(coder: Coder) {
        downloadFile(CREDS_FILENAME, generateFileContent(coder))
    }

    private suspend fun generateFileContent(coder: Coder): String {
        return Json.encodeToString(
            CredsJSON(
                login = login.value ?: error("Cannot export null creds: login"),
                privateKey = coder.exportPrivateRSAKeyPEM(
                    key = keyPair.value?.privateKey ?: error("Cannot export null creds: login")
                ),
                chatKeys = chatKeys.entries.associate { (k, v) ->
                    chatToId(k) to coder.exportPrivateRSAKeyPEM(v.privateKey)
                }
            )
        )
    }

    companion object {
        const val PK_ID = "private-key"
        const val CREDS_FILENAME = "sc.creds.json"

        private fun chatToId(id: Long) = "chat-$id"

        private fun chatFromId(id: String) = id.removePrefix("chat-").toLong()

        suspend fun parseCredsFileContent(content: String, coder: Coder): CredsDTO? {
            return try {
                val json = Json.decodeFromString<CredsJSON>(content)
                CredsDTO(
                    login = json.login,
                    privateKey = coder.importRSAPrivateKeyPEM(json.privateKey),
                    chatKeys = json.chatKeys.entries.associate { (k, v) ->
                        chatFromId(k) to coder.importRSAPrivateKeyPEM(v)
                    }
                )
            } catch (e: dynamic) {
                console.warn("Failed to parse file")
                console.warn(e)
                null
            }
        }
    }
}

@Serializable
private data class CredsJSON(
    val login: String,
    val privateKey: String,
    val chatKeys: Map<String, String>,
)

data class CredsDTO(
    val login: String,
    val privateKey: PrivateCryptoKey,
    val chatKeys: Map<Long, PrivateCryptoKey>,
)

data class ApiContext(
    val username: String,
    val privateCryptoKey: PrivateCryptoKey,
    val publicCryptoKey: PublicCryptoKey?,
    val coder: Coder,
    val chatKeys: Map<Long, CryptoKeyPair> = emptyMap(),
) : Coder by coder

fun PublicCryptoKey?.unsureKey() = this ?: error("Expected publicKey")
