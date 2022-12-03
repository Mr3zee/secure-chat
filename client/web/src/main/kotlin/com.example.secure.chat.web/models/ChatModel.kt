package com.example.secure.chat.web.models

import com.arkivanov.decompose.ComponentContext
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.components.TextInputType
import com.example.secure.chat.web.compose.mutableProperty
import com.example.secure.chat.web.models.api.ChatApi
import com.example.secure.chat.web.models.chat.*
import com.example.secure.chat.web.models.chat.processors.GlobalMessageProcessor
import com.example.secure.chat.web.models.chat.processors.LocalMessageProcessor
import com.example.secure.chat.web.models.chat.processors.MessageProcessor
import kotlinx.coroutines.Job

class ChatModel(
    private val api: ChatApi,
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    val privateKey = mutableProperty<String?>(null)

    val chats = mutableProperty(emptyList<Chat>())

    val selectedChat = mutableProperty<Chat>(Chat.Local)
    val selectedChatTimeline = mutableProperty(emptyList<Message>())

    private val localChatTimeline = mutableListOf<Message>()

    private var chatTimelineRequestJob: Job? = null

    private val messageProcessor = mutableProperty<MessageProcessor>(LocalMessageProcessor)

    val inputType = mutableProperty(TextInputType.Message)

    private val inputByChat = mutableMapOf<Chat, String>()
    val currentInput = mutableProperty("")

    init {
        launch(Ui) {
            chats.value = api.getAllChats()
        }

        currentInput.onChange {
            inputByChat[selectedChat.value] = it
        }

        selectedChat.onChangeWithPrev { prev, chat ->
            inputByChat[prev] = currentInput.value

            currentInput.value = inputByChat.getOrElse(chat) { "" }
            selectedChatTimeline.value = emptyList()

            when (chat) {
                is Chat.Local -> {
                    selectedChatTimeline.value = localChatTimeline

                    messageProcessor.value = LocalMessageProcessor
                }

                is Chat.Global -> {
                    chatTimelineRequestJob?.cancel()
                    chatTimelineRequestJob = launch(Ui) {
                        selectedChatTimeline.value = api.getChatTimeline(chat)
                    }

                    messageProcessor.value = GlobalMessageProcessor
                }
            }
        }
    }

    fun submitMessage() {
        val text = currentInput.value
        currentInput.value = ""
        if (text.isNotBlank()) {
            when (inputType.value) {
                TextInputType.Secret -> acceptUserSecret(text)
                TextInputType.Message -> acceptUserInput(text)
            }
        }
    }

    private fun acceptUserInput(text: String) {
        val message = textToMessage(text, Author.Me)

        launch(Ui) {
            dispatchMessage(selectedChat.value, message)
        }
    }

    private fun acceptUserSecret(text: String) {
        val message = textToMessage(text, Author.Me, isSecret = true) as Message.Text

        launch(Ui) {
            dispatchMessage(selectedChat.value, message.copy(text = "*".repeat(16)))
        }
    }

    private suspend fun dispatchMessage(chat: Chat, message: Message) {
        newMessage(chat, message)

        val status = try {
            val response = messageProcessor.value.processMessage(message)
            newMessage(chat, response)
            MessageStatus.Verified
        } catch (e: Exception) {
            MessageStatus.Failed
        }

        message.status.value = status
    }

    private fun newMessage(chat: Chat, message: Message) {
        if (selectedChat.value == chat) {
            selectedChatTimeline.value = listOf(message) + selectedChatTimeline.value
        }

        chat.lastMessage.value = message
    }
}
