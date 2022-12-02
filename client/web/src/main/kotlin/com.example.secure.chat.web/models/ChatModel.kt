package com.example.secure.chat.web.models

import androidx.compose.runtime.mutableStateOf
import com.example.auth.common.chat.ChatDto
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.compose.ModelContext
import com.example.secure.chat.web.models.api.ChatApi
import com.example.secure.chat.web.models.chat.Author
import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.models.chat.Message
import com.example.secure.chat.web.models.chat.processors.GlobalMessageProcessor
import com.example.secure.chat.web.models.chat.processors.LocalMessageProcessor
import com.example.secure.chat.web.models.chat.processors.MessageProcessor
import com.example.secure.chat.web.models.chat.textToMessage
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ChatModel(
    private val api: ChatApi,
    private val modelContext: ModelContext
) : ModelContext by modelContext {
    val privateKey = mutableStateOf<String?>(null)

    val chats = mutableStateOf(
        listOf(
            Chat.Local,
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(0, "Chat 1")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(0, "Chat 2")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(0, "Chat 3")),
            Chat.Global(mutableStateOf(false), mutableStateOf(null), ChatDto(0, "Chat 4")).apply {
                lastMessage.value = Message.Text(Author.Me, 0, Clock.System.now().toLocalDateTime(TimeZone.UTC), "hello text doihwiuwhiuxbwievbiwbxnewhew dhwidghiegdwigdwuiygeidgewidgewi")
            },
        )
    )

    val selectedChat = mutableStateOf<Chat>(Chat.Local)
    val selectedChatTimeline = mutableStateOf(emptyList<Message>())

    private val localChatTimeline = mutableListOf<Message>()

    private var chatTimelineRequestJob: Job? = null

    private val messageProcessor = mutableStateOf<MessageProcessor>(LocalMessageProcessor)

    init {
        selectedChat.subscribe { chat ->
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

    fun acceptUserInput(text: String) {
        val message = textToMessage(text, Author.Me)

        launch(Ui) {
            dispatchMessage(selectedChat.value, message)
        }
    }

    private suspend fun dispatchMessage(chat: Chat, message: Message) {
        newMessage(chat, message)
        val response = messageProcessor.value.processMessage(message)
        newMessage(chat, response)
    }

    private fun newMessage(chat: Chat, message: Message) {
        if (selectedChat.value == chat) {
            selectedChatTimeline.value += message
        }

        chat.lastMessage.value = message
    }
}
