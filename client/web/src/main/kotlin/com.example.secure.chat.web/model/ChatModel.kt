package com.example.secure.chat.web.model

import com.arkivanov.decompose.ComponentContext
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.crypto.CryptoKeyPair
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.model.api.ChatApi
import com.example.secure.chat.web.model.chat.*
import com.example.secure.chat.web.model.chat.processors.*
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.Credentials
import com.example.secure.chat.web.utils.clipboard
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

    val newMessageEvent = mutableProperty(Unit)

    val localMessageProcessor = LocalMessageProcessor(this)
    private val globalMessageProcessor = GlobalMessageProcessor(this)

    private val messageProcessor = mutableProperty<MessageProcessor>(localMessageProcessor)


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
                        selectedChatTimeline.value = api.getChatTimeline(chat)
                        selectedChat.value.lastMessage.value = selectedChatTimeline.value.lastOrNull()

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

    fun logout() {
        selectedChat.value = Chat.Local
        inputByChat.clear()
        chats.value = emptyMap()
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
            newMessage(selectedChat.value, Message(securityManagerBot, "Failed to upload a file."))

            unlockInput()
        }

        reader.readAsText(file)
    }

    suspend fun loadChats() {
        api.getAllChats(coder).let { list ->
            chats.value = list.associateBy(keySelector = { it.first.id }) { it.first }
            credentials.chatsLonePublicKeys.putAll(list.associateBy(keySelector = { it.first.id }) { it.second })
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

    fun addChat(chat: Chat.Global, keyPair: CryptoKeyPair) {
        chats.value += chat.id to chat
        credentials.chatKeys += chat.id to keyPair
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
        newMessage(chat, message)

        with(messageProcessor.value) {
            val context = MessageContext(message.author) {
                newMessage(chat, it)
            }

            context.processMessage(message)
        }
    }

    private fun newMessage(chat: Chat, message: Message) {
        if (selectedChat.value == chat) {
            selectedChatTimeline.value += message
        }

        if (chat is Chat.Local) {
            localChatTimeline += message
        }

        chat.lastMessage.value = message

        newMessageEvent.fire()
    }
}

enum class ChatInputType {
    Message, Secret, Copy, File
}
