package com.example.secure.chat.web.model.chat.processors

import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.crypto.*
import com.example.secure.chat.web.model.ChatModel
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.MessageStatus
import com.example.secure.chat.web.model.chat.processors.handler.ConversationHandler
import com.example.secure.chat.web.model.creds.Credentials
import com.example.secure.chat.web.model.creds.CredsDTO

class LocalMessageProcessor(
    private val model: ChatModel,
) : MessageProcessor {
    private val storage = mutableMapOf<String, String>()

    override suspend fun createMessage(text: String): Message {
        return message(text)
    }

    override suspend fun MessageContext.processMessage(message: Message): Unit = with(localConversation) {
        model.lockInput()

        handle(message) ?: run {
            sendMessage("Unknown command. If you are not logged in, please log in or register first. List of available commands:")
            sendMessage(CMD_REFERENCE)
        }

        message.status.value = MessageStatus.Verified

        model.unlockInput()
    }

    private val usernameRegex = Regex("^[a-zA-Z0-9_\\-]{5,}$")

    private val localConversation = ConversationHandler(LocalState.START, securityManagerBot) {
        state(LocalState.START) {
            command(LocalCommands.register.name) {
                it.args.singleOrNull() ?: run {
                    sendMessage("Please, provide exactly one argument - desired username.")
                    return@command LocalState.START
                }

                val (pk, sk) = model.coder.genRsaKeyPair()
                val login = it.args[0]

                if (model.api.registerUser(login, pk, model.coder)) {
                    model.credentials.privateKey.value = sk
                    model.credentials.login.value = login

                    model.displaySecret(Credentials.PK_ID, model.coder.exportPrivateRSAKeyPEM(sk))

                    model.prepareSecretToCopy(sk)

                    sendMessage(
                        """
                            User profile created. Please, use the button below to copy your secret to the clipboard or save as a file.
                        """.trimIndent()
                    )

                    LocalState.LOGGED_IN
                } else {
                    sendMessage("Username is already taken, please choose another.")
                    LocalState.START
                }
            }

            command(LocalCommands.login.name) {
                sendMessage("Please, enter your username.")

                LocalState.AWAIT_USERNAME
            }

            command(LocalCommands.file_login.name) {
                sendMessage("Please, use the button bellow to select a file.")

                model.acceptFile()

                LocalState.AWAIT_LOGIN_FILE
            }
        }

        state(LocalState.AWAIT_USERNAME) {
            cancel(LocalState.START)

            text {
                if (usernameRegex.matches(it.text)) {
                    sendMessage(
                        """
                            Please, enter your private key ('${Credentials.PK_ID}' section in the ${Credentials.CREDS_FILENAME} file).
                        """.trimIndent()
                    )

                    storage[USERNAME_STORAGE_KEY] = it.text

                    model.acceptSecret()

                    LocalState.AWAIT_PK
                } else {
                    sendMessage("Invalid username, please use with regex as a reference: ${usernameRegex.pattern}")
                    LocalState.AWAIT_USERNAME
                }
            }
        }

        state(LocalState.AWAIT_PK) {
            cancel(LocalState.START)

            text {
                val username = storage[USERNAME_STORAGE_KEY] ?: run {
                    console.warn("Lost username in conversation storage.")

                    sendMessage("Something went wrong, please enter your username again.")

                    return@text LocalState.AWAIT_USERNAME
                }

                val privateCryptoKey = model.coder.safeImportRSAPrivateKeyPEM(it.text) ?: run {
                    sendMessage("Invalid private key. Please, try again.")

                    model.acceptSecret()

                    return@text LocalState.AWAIT_PK
                }

                storage.clear()

                if (model.api.loginUser(username, privateCryptoKey, model.coder)) {
                    model.credentials.privateKey.value = privateCryptoKey
                    model.credentials.login.value = username

                    launch(Ui) {
                        model.loadChats()
                    }

                    sendMessage("Login successful. Welcome, $username!")

                    LocalState.LOGGED_IN
                } else {
                    sendMessage("Failed to log in. Please, check your credentials and try again.")
                    sendMessage("Please, enter your username.")

                    LocalState.AWAIT_USERNAME
                }
            }
        }

        state(LocalState.AWAIT_LOGIN_FILE) {
            text {
                withFile(it.text, errorState = LocalState.START) { creds ->
                    if (model.api.loginUser(creds.login, creds.privateKey, model.coder)) {
                        sendMessage("Login successful. Welcome, ${creds.login}!")

                        model.credentials.login.value = creds.login
                        model.credentials.privateKey.value = creds.privateKey

                        launch(Ui) {
                            model.loadChats()

                            loginIntoChats(creds)
                        }

                        LocalState.LOGGED_IN
                    } else {
                        sendMessage("Invalid user credentials. Aborting.")

                        LocalState.START
                    }
                }
            }
        }

        state(LocalState.LOGGED_IN) {
            command(LocalCommands.logout.name) {
                sendMessage("Successfully logged out. Bye, ${model.credentials.login.value}!")

                model.credentials.clear()
                model.logout()

                LocalState.START
            }

            command(LocalCommands.new_chat.name) {
                val chatName = it.args.singleOrNull() ?: run {
                    sendMessage("Please, provide exactly one argument - chat name.")

                    return@command LocalState.LOGGED_IN
                }

                val initialMessage = Message(
                    author = author,
                    text = "${author.name} created this chat",
                    initialStatus = MessageStatus.Verified
                )

                val (chat, sk) = model.api.createChat(chatName, initialMessage, model.coder)

                model.prepareSecretToCopy(sk)

                sendMessage("Created new chat '$chatName'. Please, use the button bellow to copy the chat's secret.")

                model.addChat(chat, sk)

                LocalState.LOGGED_IN
            }

            command(LocalCommands.chat_login.name) {
                val chatId = it.args.singleOrNull()?.toLongOrNull() ?: run {
                    sendMessage("Please, provide exactly one integer argument - chat id.")

                    return@command LocalState.LOGGED_IN
                }

                if (!model.chats.value.containsKey(chatId)) {
                    sendMessage("Invalid chat id.")

                    return@command LocalState.LOGGED_IN
                }

                sendMessage("Please, enter chat's private key.")

                storage[CHAT_NAME_STORAGE_KEY] = chatId.toString()

                model.acceptSecret()

                LocalState.AWAIT_CHAT_PK
            }

            command(LocalCommands.dump.name) {
                launch(Ui) {
                    model.credentials.dumpFile(model.coder)
                }

                sendMessage("Generating and downloading file.")

                LocalState.LOGGED_IN
            }

            command(LocalCommands.load.name) {
                sendMessage("Please, use the button bellow to select a file.")

                model.acceptFile()

                LocalState.AWAIT_FILE
            }
        }

        state(LocalState.AWAIT_CHAT_PK) {
            cancel(LocalState.LOGGED_IN)

            text {
                val privateKey = model.coder.safeImportRSAPrivateKeyPEM(it.text) ?: run {
                    sendMessage("Invalid private key. Please, try again.")

                    model.acceptSecret()

                    return@text LocalState.AWAIT_PK
                }

                val chatId = storage[CHAT_NAME_STORAGE_KEY]?.toLongOrNull() ?: run {
                    console.warn("$CHAT_NAME_STORAGE_KEY was not found")

                    sendMessage("Something went wrong. Aborting operation.")

                    return@text LocalState.LOGGED_IN
                }

                storage.clear()

                val chat = model.chats.value[chatId] ?: run {
                    sendMessage("Chat with $chatId was not found. Please, try again.")

                    return@text LocalState.LOGGED_IN
                }

                if (tryLoginIntoChat(chat, privateKey)) {
                    model.credentials.chatKeys.value += chatId to privateKey
                }

                LocalState.LOGGED_IN
            }
        }

        state(LocalState.AWAIT_FILE) {
            text {
                withFile(it.text, LocalState.LOGGED_IN) { creds ->
                    loginIntoChats(creds)

                    LocalState.LOGGED_IN
                }
            }
        }
    }

    private suspend fun MessageContext.loginIntoChats(credsDTO: CredsDTO) {
        val chats = model.chats.value
        val unused = mutableListOf<Long>()

        credsDTO.chatKeys.entries.forEach { (k, v) ->
            chats[k]?.let { chat ->
                tryLoginIntoChat(chat, v)
            } ?: run {
                unused.add(k)
            }
        }

        model.credentials.chatKeys.value -= unused
    }

    private suspend fun MessageContext.tryLoginIntoChat(
        chat: Chat.Global,
        privateCryptoKey: PrivateCryptoKey,
    ): Boolean {
        val message = model.api.getLastMessage(chat, privateCryptoKey, model.coder) ?: run {
            sendMessage("Failed to login into ${chat.name} chat.")
            return false
        }

        sendMessage("Logged in into ${chat.name} chat.")

        chat.lastMessage.value = message
        chat.isLocked.value = false

        return true
    }

    private suspend fun MessageContext.withFile(
        text: String,
        errorState: LocalState,
        handler: suspend MessageContext.(CredsDTO) -> LocalState,
    ): LocalState {
        return Credentials.parseCredsFileContent(text, model.coder)?.let { creds ->
            handler(creds)
        } ?: run {
            sendMessage("Invalid file, please use a file generated by this app. Aborting.")

            errorState
        }
    }

    private fun MessageContext.sendMessage(string: String) {
        dispatch(message(string))
    }

    companion object {
        val CMD_REFERENCE = """
/${LocalCommands.login} - login into your profile.
/${LocalCommands.file_login} - login using ${Credentials.CREDS_FILENAME} file.
/${LocalCommands.register} <username> - create a new profile.
/${LocalCommands.new_chat} <name> - create a new chat.
/${LocalCommands.chat_login} <id> - login into the chat.
/${LocalCommands.logout} - logout from current profile.
/${LocalCommands.dump} - download ${Credentials.CREDS_FILENAME} file with all keys.
/${LocalCommands.load} - upload ${Credentials.CREDS_FILENAME} file.
/cancel - abort current operation.
        """.trimIndent()

        private const val USERNAME_STORAGE_KEY = "username"
        private const val CHAT_NAME_STORAGE_KEY = "chat"
    }
}

private enum class LocalState {
    START, LOGGED_IN, AWAIT_USERNAME, AWAIT_PK, AWAIT_LOGIN_FILE, AWAIT_CHAT_PK, AWAIT_FILE
}

@Suppress("EnumEntryName")
private enum class LocalCommands {
    login, file_login, logout, register, new_chat, chat_login, dump, load
}

private fun message(text: String) = Message(
    author = securityManagerBot,
    text = text,
    initialStatus = MessageStatus.Verified
)
