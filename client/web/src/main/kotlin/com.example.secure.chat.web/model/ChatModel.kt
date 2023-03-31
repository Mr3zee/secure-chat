package com.example.secure.chat.web.model

import com.arkivanov.decompose.ComponentContext
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.platform.safeLaunch
import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.model.api.ChatApi
import com.example.secure.chat.web.model.chat.*
import com.example.secure.chat.web.model.chat.processors.*
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.ApiContext
import com.example.secure.chat.web.model.creds.Credentials
import com.example.secure.chat.web.utils.clipboard
import com.example.secure.chat.web.utils.get
import kotlinx.coroutines.Job
import org.w3c.files.File
import org.w3c.files.FileReader

class ChatModel(
    val credentials: Credentials,
    val api: ChatApi,
    val coder: Coder,
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    // all chats
    val chats = mutableProperty(emptyMap<Long, Chat.Global>())

    // currently displayed chat
    val selectedChat = mutableProperty<Chat>(Chat.Local)

    // currently displayed chat's timeline
    val selectedChatTimeline = mutableProperty(emptyList<Message>())
    private val localChatTimeline = mutableListOf<Message>()
    private var chatTimelineRequestJob: Job? = null

    // input properties
    val inputLocked = mutableProperty(false)
    val inputType = mutableProperty(ChatInputType.Message)
    val currentInput = mutableProperty("")
    val resetInput = mutableProperty(currentInput.value)
    private val inputByChat = mutableMapOf<Chat, String>()

    // all invites
    val invites = mutableProperty(emptyMap<Long, Invite>())

    val newMessageEvent = mutableProperty(Unit)

    private val localMessageProcessor = LocalMessageProcessor(this)
    private val globalMessageProcessor = GlobalMessageProcessor(this)

    private val messageProcessor = mutableProperty<MessageProcessor>(localMessageProcessor)

    val apiContext: ApiContext get() = credentials.loginContext(coder)

    init {
        resetInput.subscribe {
            currentInput.value = it
        }

        currentInput.subscribe {
            inputByChat[selectedChat.value] = it
        }

        selectedChat.subscribeWithPrev { prev, chat ->
            inputByChat[prev] = currentInput.value

            resetInput.value = inputByChat.getOrElse(chat) { "" }
            selectedChatTimeline.value = emptyList()

            chat.lastMessage.value?.status?.let {
                if (it.value == MessageStatus.Unread) {
                    it.value = MessageStatus.Verified
                }
            }

            when (chat) {
                is Chat.Local -> {
                    selectedChatTimeline.value = localChatTimeline

                    messageProcessor.value = localMessageProcessor
                }

                is Chat.Global -> {
                    chatTimelineRequestJob?.cancel()
                    chatTimelineRequestJob = launch(Ui) {
                        selectedChatTimeline.value = api.getChatTimeline(apiContext, chat).let {
                            if (it.isFailure) {
                                console.error("Failed to load chat's timeline")
                                emptyList()
                            } else it.get()
                        }
                        selectedChat.value.lastMessage.value = selectedChatTimeline.value.firstOrNull()

                        chat.lastMessage.value?.status?.let {
                            if (it.value == MessageStatus.Unread) {
                                it.value = MessageStatus.Verified
                            }
                        }
                    }

                    messageProcessor.value = globalMessageProcessor
                }
            }
        }
    }

    private fun subscribeOnServerEvents() {
        api.subscribeOnNewInvites(apiContext) {
            invites.value += it.associateBy { invite -> invite.chatId }
        }

        api.subscribeOnNewMessages(apiContext) { messages ->
            val chats = chats.value
            messages.forEach { (chatId, message) ->
                chats[chatId]?.let { chat ->
                    newMessage(chat, message)
                }
            }
        }
    }

    suspend fun leaveChat(chat: Chat.Global): Boolean {
        // todo unsubscribe

        return if (api.leaveChat(apiContext, chat)) {
            if (chat == selectedChat.value) {
                selectedChat.value = Chat.Local
            }

            chats.value -= chat.id
            credentials.chatsLonePublicKeys -= chat.id
            credentials.chatKeys -= chat.id

            true
        } else false
    }

    suspend fun logout() {
        selectedChat.value = Chat.Local
        inputByChat.clear()
        chats.value = emptyMap()
        // todo unsubscribe

        api.logoutUser()
    }

    fun prepareFileInput() {
        inputType.value = ChatInputType.File
    }

    fun acceptFile(file: File) {
        lockInput()
        val reader = FileReader()
        reader.onload = {
            val text = it.target.asDynamic().result as String

            inputType.value = ChatInputType.Secret
            currentInput.value = text
            submitMessage()

            unlockInput()
        }

        reader.onerror = {
            sendLocalMessage("Failed to upload a file.")

            unlockInput()
        }

        reader.readAsText(file)
    }

    suspend fun loadChats() {
        api.getAllChats(apiContext).let { res ->
            val list = if (res.isFailure) {
                console.error("Failed to load chat list")
                return
            } else res.get()

            chats.value = list.associateBy(keySelector = { it.first.id }) { it.first }
            chats.value.values.forEach { api.addChatSubscription(apiContext, it) }
            credentials.chatsLonePublicKeys.putAll(list.associateBy(keySelector = { it.first.id }) { it.second })
        }
    }

    private fun loadChatLastMessage(chat: Chat.Global) {
        val pk = credentials.chatKeys[chat.id]?.privateKey ?: return

        safeLaunch(Ui) {
            chat.lastMessage.value = api.getLastMessage(apiContext, chat, pk).get().first
        }
    }

    suspend fun acceptInvite(chatName: String, id: Invite): Boolean {
        val res = api.acceptInvite(apiContext, chatName, id)

        if (res.isFailure) {
            return false
        }

        val (chat, keyPair) = res.getOrNull() ?: error("unreachable")
        addChat(chat, keyPair)
        loadChatLastMessage(chat)
        prepareSecretToCopy(keyPair.privateKey)

        return true
    }

    fun onLogin(login: String, keyPair: CryptoKeyPair) {
        credentials.login.value = login
        credentials.keyPair.value = keyPair
        subscribeOnServerEvents()
        safeLaunch(Ui) {
            loadInvites()
        }
    }

    private suspend fun loadInvites() {
        invites.value = api.listInvites(apiContext).let {
            if (it.isFailure) throw it.exceptionOrNull()!!

            it.get().associateBy { invite -> invite.chatId }
        }
    }

    fun lockInput() {
        inputLocked.value = true
    }

    fun unlockInput() {
        inputLocked.value = false
    }

    fun prepareSecretInput() {
        inputType.value = ChatInputType.Secret
    }

    suspend fun addChat(chat: Chat.Global, keyPair: CryptoKeyPair) {
        api.addChatSubscription(apiContext, chat)

        chats.value += chat.id to chat
        credentials.chatKeys += chat.id to keyPair

        chat.isLocked.value = false
    }

    fun cancelFileUpload() {
        currentInput.value = "/cancel"

        submitMessage()
    }

    private val secretToCopy = mutableProperty<String?>(null)

    suspend fun prepareSecretToCopy(secret: PrivateCryptoKey) {
        secretToCopy.value = coder.exportPrivateRSAKeyPEM(secret)
        inputType.value = ChatInputType.Copy
    }

    fun copySecretToClipboard() {
        inputType.value = ChatInputType.Message
        secretToCopy.value?.let {
            clipboard.writeText(it)
        }
        secretToCopy.value = null
    }

    fun submitMessage() {
        val text = currentInput.value
        if (text.isBlank()) return

        resetInput.value = ""
        val inputTypeVal = inputType.value
        inputType.value = ChatInputType.Message
        acceptUserInput(text, inputTypeVal)
    }

    private fun acceptUserInput(text: String, inputType: ChatInputType) {
        val message = textToMessage(text, Author.Me, inputType == ChatInputType.Secret)

        launch(Ui) {
            dispatchMessage(selectedChat.value, message)
        }
    }

    private suspend fun dispatchMessage(chat: Chat, message: Message) {
        with(messageProcessor.value) {
            val user = credentials.login.value?.let { Author.User(it) } ?: Author.Me

            val context = MessageContext(chat, user, message) {
                newMessage(chat, it)
            }

            context.processMessage()
        }
    }

    private fun newMessage(chat: Chat, message: Message) {
        if (selectedChat.value == chat && !selectedChatTimeline.value.contains(message)) {
            selectedChatTimeline.value += message

            newMessageEvent.fire()
        }

        if (chat is Chat.Local) {
            localChatTimeline += message
        }

        chat.lastMessage.value = message
    }

    private fun sendLocalMessage(text: String) {
        newMessage(
            chat = Chat.Local,
            message = Message(
                author = securityManagerBot,
                text = text,
                initialStatus = MessageStatus.Local
            )
        )
    }
}

enum class ChatInputType {
    Message, Secret, Copy, File
}
