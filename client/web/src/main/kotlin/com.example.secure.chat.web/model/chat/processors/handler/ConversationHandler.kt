package com.example.secure.chat.web.model.chat.processors.handler

import com.example.secure.chat.web.model.chat.Author
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.processors.MessageContext

class ConversationHandler<E : Enum<E>>(
    startState: E,
    bot: Author.Bot? = null,
    conversation: ConversationBuilder<E>.() -> Unit,
) : Handler<E> {
    fun setState(state: E) {
        currentState = state
    }

    private var currentState = startState

    private val conversation = ConversationBuilder<E>(bot).also(conversation).build()

    override suspend fun MessageContext.handle(message: Message): E? {
        return conversation.states[currentState]?.let { handlers ->
            handlers.forEach {
                with(it) {
                    handle(message)?.let { newState ->
                        currentState = newState
                        return newState
                    }
                }
            }

            return null
        }
    }
}

data class Conversation<E : Enum<E>>(
    val states: Map<E, List<Handler<E>>>,
)

class ConversationBuilder<E : Enum<E>>(private val bot: Author.Bot?) {
    private val states = mutableMapOf<E, List<Handler<E>>>()

    fun state(state: E, stateBuilder: ConversationStateBuilder<E>.() -> Unit) {
        val list = ConversationStateBuilder<E>(bot).also(stateBuilder).build()
        states[state] = list
    }

    fun build() = Conversation(states)
}

class ConversationStateBuilder<E : Enum<E>>(private val bot: Author.Bot?) {
    private val handlers = mutableListOf<Handler<E>>()

    fun text(handler: suspend MessageContext.(Message) -> E?) {
        handlers.add(TextHandler(handler))
    }

    fun command(handlerCommand: String, handler: suspend MessageContext.(CommandMessage) -> E?) {
        handlers.add(CommandHandler(handlerCommand, handler))
    }

    fun cancel(state: E) {
        bot?.let {
            command("cancel") {
                dispatch(Message(bot, "Operation canceled."))

                state
            }
        }
    }

    fun build(): List<Handler<E>> = handlers
}
