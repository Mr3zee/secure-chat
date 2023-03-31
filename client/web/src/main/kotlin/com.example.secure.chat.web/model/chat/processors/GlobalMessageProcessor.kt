package com.example.secure.chat.web.model.chat.processors

import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.model.ChatModel
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.MessageStatus
import com.example.secure.chat.web.model.chat.processors.handler.ConversationHandler

class GlobalMessageProcessor(private val model: ChatModel) : MessageProcessor {
    override suspend fun MessageContext.processMessage(): Unit = with(globalConversation) {
        try {
            when (handle(message) ?: error("unreachable")) {
                GlobalState.OK, GlobalState.DEFAULT -> MessageStatus.Verified

                GlobalState.ERROR -> MessageStatus.Failed

                else -> null
            }?.let {
                message.status.value = it
            }
        } catch (e: dynamic) {
            localMessage("Failed to perform operation.")
            console.error(e)
        } finally {
            globalConversation.setState(GlobalState.DEFAULT)
        }
    }

    private val globalConversation = ConversationHandler(GlobalState.DEFAULT) {
        state(GlobalState.DEFAULT) {
            command(GlobalCommand.leave.name) {
                if (chat !is Chat.Global) {
                    console.warn("Attempt to leave the Local chat.")

                    return@command GlobalState.ERROR
                }

                localMessage("Processing your request.")

                if (model.leaveChat(chat)) {
                    globalMessage("User '${author.name}' left the chat.")

                    GlobalState.OK
                } else {
                    localMessage("Failed to leave the chat.")

                    GlobalState.ERROR
                }
            }

            command(GlobalCommand.invite.name) {
                if (chat !is Chat.Global) {
                    console.warn("Attempt to invite to the Local chat.")

                    return@command GlobalState.ERROR
                }

                val username = it.args.singleOrNull() ?: run {
                    localMessage("Expected exactly one argument - username to invite.")

                    return@command GlobalState.ERROR
                }

                if (model.api.inviteMember(model.apiContext, chat, username)) {
                    globalMessage("'${author.name}' invited '$username' to the chat.")

                    GlobalState.OK
                } else {
                    localMessage("Failed to invite '$username'.")

                    GlobalState.ERROR
                }
            }

            text {
                if (chat !is Chat.Global) {
                    console.warn("Attempt the send a global message to the Local chat.")

                    return@text GlobalState.ERROR
                }

                sendGlobalMessage(chat, message)

                GlobalState.PENDING
            }
        }
    }

    private fun MessageContext.globalMessage(text: String) {
        if (chat !is Chat.Global) {
            console.warn("Attempt to send a global message in the local chat")
            return
        }

        val message = Message(
            author = author,
            text = text,
            initialStatus = MessageStatus.Pending
        )

        sendGlobalMessage(chat, message)
    }

    private fun MessageContext.sendGlobalMessage(chat: Chat.Global, globalMessage: Message) {
        launch(Ui) {
            model.api.sendMessage(model.apiContext, chat, globalMessage)?.let {
                globalMessage.status.value = MessageStatus.Verified
                globalMessage.id = it.id
            } ?: run {
                globalMessage.status.value = MessageStatus.Failed
            }
        }

        dispatch(globalMessage)
    }

    private fun MessageContext.localMessage(text: String) {
        val message = Message(
            author = securityManagerBot,
            text = text,
            initialStatus = MessageStatus.Local
        )

        dispatch(message)
    }

    companion object {
        val CMD_REFERENCE = """
/${GlobalCommand.invite} <username> - invite new members. 
/${GlobalCommand.leave} - leave chat.
        """.trimIndent()
    }
}


private enum class GlobalState {
    DEFAULT, OK, ERROR, PENDING
}

@Suppress("EnumEntryName")
private enum class GlobalCommand {
    leave, invite
}
