package com.example.secure.chat.web.model

import com.arkivanov.decompose.ComponentContext
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.crypto.PrivateCryptoKey
import com.example.secure.chat.web.model.api.ChatApi
import com.example.secure.chat.web.model.chat.*
import com.example.secure.chat.web.model.chat.processors.GlobalMessageProcessor
import com.example.secure.chat.web.model.chat.processors.LocalMessageProcessor
import com.example.secure.chat.web.model.chat.processors.MessageContext
import com.example.secure.chat.web.model.chat.processors.MessageProcessor
import com.example.secure.chat.web.model.coder.Coder
import com.example.secure.chat.web.model.creds.Credentials
import com.example.secure.chat.web.utils.clipboard
import kotlinx.coroutines.Job

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

    fun displaySecret(name: String, secret: String) {
        // todo
    }

    fun acceptFile() {
        // todo
    }

    suspend fun loadChats() {
        chats.value = api.getAllChats().associateBy { it.id }
    }

    fun lockInput() {
        // todo
    }

    fun unlockInput() {
        // todo
    }

    fun acceptSecret() {
        inputType.value = ChatInputType.Secret
    }

    fun addChat(chat: Chat.Global, privateCryptoKey: PrivateCryptoKey) {
        chats.value += chat.id to chat
        credentials.chatKeys.value += chat.id to privateCryptoKey
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
