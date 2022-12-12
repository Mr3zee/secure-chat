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
import com.example.secure.chat.web.model.creds.LoginContext
import kotlinx.js.jso

class LocalMessageProcessor(
    private val model: ChatModel,
) : MessageProcessor {
    private val storage = mutableMapOf<String, String>()

    override suspend fun MessageContext.processMessage(): Unit = with(localConversation) {
        message.status.value = MessageStatus.Local
        dispatch(message)

        model.lockInput()

        try {
            handle(message) ?: run {
                sendMessage("Unknown command. If you are not logged in, please log in or register first. List of available commands:")
                sendMessage(CMD_REFERENCE)
            }
        } catch (e: dynamic) {
            sendMessage("Failed to perform operation.")
        } finally {
            model.unlockInput()
        }
    }

    private val usernameRegex = Regex("^[a-zA-Z0-9_\\-]{5,}$")

    private val localConversation = ConversationHandler(LocalState.START, securityManagerBot) {
        state(LocalState.START) {
            command(LocalCommands.register.name) {
                it.args.singleOrNull() ?: run {
                    sendMessage("Please, provide exactly one argument - desired username.")
                    return@command LocalState.START
                }

                val keyPair = model.coder.genRsaKeyPair()
                val login = it.args[0]

                if (model.api.registerUser(login, keyPair, model.coder)) {
                    model.credentials.keyPair.value = keyPair
                    model.credentials.login.value = login

                    model.onLogin()
                    model.prepareSecretToCopy(keyPair.privateKey)

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

                model.prepareFileInput()

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

                    model.prepareSecretInput()

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

                    model.prepareSecretInput()

                    return@text LocalState.AWAIT_PK
                }

                storage.clear()

                val res = model.api.loginUser(LoginContext(username, privateCryptoKey, model.coder))

                when {
                    res.isSuccess -> {
                        val keyPair = res.getOrNull() ?: error("unreachable")

                        model.credentials.keyPair.value = keyPair
                        model.credentials.login.value = username

                        launch(Ui) {
                            model.loadChats()
                        }

                        model.onLogin()

                        sendMessage("Login successful. Welcome, $username!")

                        LocalState.LOGGED_IN
                    }

                    else -> {
                        sendMessage("Failed to log in. Please, check your credentials and try again.")
                        sendMessage("Please, enter your username.")

                        LocalState.AWAIT_USERNAME
                    }
                }
            }
        }

        state(LocalState.AWAIT_LOGIN_FILE) {
            cancel(LocalState.START)

            text {
                withFile(it.text, errorState = LocalState.START) { creds ->
                    val res = model.api.loginUser(LoginContext(creds.login, creds.privateKey, model.coder))

                    when {
                        res.isSuccess -> {
                            val keyPair = res.getOrNull() ?: error("unreachable")

                            sendMessage("Login successful. Welcome, ${creds.login}!")

                            model.credentials.login.value = creds.login
                            model.credentials.keyPair.value = keyPair

                            launch(Ui) {
                                model.loadChats()

                                loginIntoChats(creds)
                            }

                            LocalState.LOGGED_IN
                        }

                        else -> {
                            sendMessage("Invalid user credentials. Aborting.")

                            LocalState.START
                        }
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

            command(LocalCommands.invites.name) {
                val list = model.invites.value.values.joinToString("\n") {
                    "Invite to chat ${it.chatId}."
                }

                if (list.isBlank()) {
                    sendMessage("No invites available.")
                } else {
                    sendMessage(list)
                }

                LocalState.LOGGED_IN
            }

            command(LocalCommands.accept.name) {
                val id = it.args.getOrNull(0)?.toLongOrNull()
                val chatName = it.args.getOrNull(1)

                if (it.args.size != 2 || id == null || chatName == null) {
                    sendMessage("Please, provide exactly two arguments (int, string) - invite id like in the /invites command.")

                    return@command LocalState.LOGGED_IN
                }

                val inviteId = model.invites.value[id] ?: run {
                    sendMessage("Invalid invite id.")

                    return@command LocalState.LOGGED_IN
                }

                if (model.acceptInvite(chatName, inviteId)) {
                    sendMessage("Invite accepted successfully.")
                } else {
                    sendMessage("Failed to accept the invite.")
                }

                LocalState.LOGGED_IN
            }

            command(LocalCommands.new_chat.name) {
                val chatName = it.args.singleOrNull() ?: run {
                    sendMessage("Please, provide exactly one argument - chat name.")

                    return@command LocalState.LOGGED_IN
                }

                val initialMessage = Message(
                    author = author,
                    text = "'${author.name}' created this chat",
                    initialStatus = MessageStatus.Verified
                )

                val (chat, keyPair) = model.api.createChat(model.loginContext, chatName, initialMessage)

                model.prepareSecretToCopy(keyPair.privateKey)

                sendMessage("Created new chat '$chatName'. Please, use the button bellow to copy the chat's secret.")

                model.addChat(chat, keyPair)

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

                model.prepareSecretInput()

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

                model.prepareFileInput()

                LocalState.AWAIT_FILE
            }
        }

        state(LocalState.AWAIT_CHAT_PK) {
            cancel(LocalState.LOGGED_IN)

            text {
                val privateKey = model.coder.safeImportRSAPrivateKeyPEM(it.text) ?: run {
                    sendMessage("Invalid private key. Please, try again.")

                    model.prepareSecretInput()

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

                val publicKey = model.credentials.chatsLonePublicKeys[chatId] ?: run {
                    console.warn("Expected public key to be present.")

                    sendMessage("Unexpected error happened, performing update...")

                    model.loadChats()

                    sendMessage("Updated, please try again with /${LocalCommands.chat_login} command.")

                    return@text LocalState.LOGGED_IN
                }

                if (tryLoginIntoChat(chat, privateKey)) {
                    model.credentials.chatKeys += chatId to jso {
                        this.privateKey = privateKey
                        this.publicKey = publicKey
                    }
                }

                LocalState.LOGGED_IN
            }
        }

        state(LocalState.AWAIT_FILE) {
            cancel(LocalState.LOGGED_IN)

            text {
                withFile(it.text, LocalState.LOGGED_IN) { creds ->
                    sendMessage("Found ${creds.chatKeys.size} chat keys.")

                    loginIntoChats(creds)

                    LocalState.LOGGED_IN
                }
            }
        }
    }

    private suspend fun MessageContext.loginIntoChats(credsDTO: CredsDTO) {
        val chats = model.chats.value
        val unused = mutableSetOf<Long>()

        credsDTO.chatKeys.entries.forEach { (k, v) ->
            chats[k]?.let { chat ->
                tryLoginIntoChat(chat, v)
            } ?: run {
                unused.add(k)
            }
        }

        model.credentials.chatKeys -= unused
    }

    private suspend fun MessageContext.tryLoginIntoChat(
        chat: Chat.Global,
        privateCryptoKey: PrivateCryptoKey,
    ): Boolean {
        val lastMessage = model.api.getLastMessage(model.loginContext, chat, privateCryptoKey) ?: run {
            sendMessage("Failed to login into ${chat.name} chat.")
            return false
        }

        sendMessage("Logged in into ${chat.name} chat.")

        chat.lastMessage.value = lastMessage
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
        dispatch(botMessage(string))
    }

    companion object {
        val CMD_REFERENCE = """
/${LocalCommands.login} - login into your profile.
/${LocalCommands.file_login} - login using ${Credentials.CREDS_FILENAME} file.
/${LocalCommands.register} <username> - create a new profile.
/${LocalCommands.new_chat} <name> - create a new chat with name <name>.
/${LocalCommands.chat_login} <id> - login into the chat with <id>.
/${LocalCommands.logout} - logout from current profile.
/${LocalCommands.dump} - download ${Credentials.CREDS_FILENAME} file with all keys.
/${LocalCommands.load} - upload ${Credentials.CREDS_FILENAME} file.
/${LocalCommands.invites} - list all available invites.
/${LocalCommands.accept} <id> <chat_name> - accept invite with <id> as chat with name <chat_name>.
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
    login, file_login, logout, register, new_chat, chat_login, dump, load, invites, accept
}

private fun botMessage(text: String) = Message(
    author = securityManagerBot,
    text = text,
    initialStatus = MessageStatus.Local
)
