package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.base.components.*
import com.example.secure.chat.web.font.FontSize
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.font.fonts.JetBrainsMono
import com.example.secure.chat.web.models.ChatModel
import com.example.secure.chat.web.models.chat.Author
import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.models.chat.Message
import com.example.secure.chat.web.theme.XTheme
import com.example.secure.chat.web.utils.capitalized
import com.example.secure.chat.web.utils.ensureScrollTo
import com.example.secure.chat.web.utils.today
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollBehavior

@Composable
fun xChatTimeline(model: ChatModel) {
    flex(
        styleBuilder = {
            height(100.percent)
            minHeight(0.px)
        }
    ) {
        val allMessages by remember { model.selectedChatTimeline.asState() }

        if (allMessages.isNotEmpty()) {
            xScrollable(
                styleBuilder = {
                    flex(1, 0, auto.unsafeCast<CSSNumeric>())

                    width(100.percent)
                },
            ) {
                xMessagesList(model, allMessages)
            }
        } else {
            xEmptyChatMessage(model)
        }
    }
}

@Composable
private fun xEmptyChatMessage(model: ChatModel) {
    flex(
        styleBuilder = {
            width(100.percent)
            height(100.percent)

            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)

            whiteSpace("pre-wrap")

            textAlign("center")
        }
    ) {
        val selectedChat by remember { model.selectedChat.asState() }

        val text = when (selectedChat) {
            is Chat.Local -> """
                This is your Local Security Manager.
                You can manage your chats here and do some other staff.
                List of available commands:
                
                /start - login into your profile.
                /register <username> - create new profile.
                /chat <name> - create new chat.
                
                If you lose your key - you will not be able to recover it.
                If you lose chat key - you will need to get a new invite.
            """.trimIndent()

            is Chat.Global -> """
                This is the start of your conversation in ${(selectedChat as? Chat.Global)?.dto?.name ?: ""}.
                List of available commands:
                
                /invite <username> - invite new members. 
                /leave - leave chat. 
                
                You will not be able to kick someone out.
                You will not be able to join back unless invited again.
            """.trimIndent()
        }

        Text(text)
    }
}

@Composable
private fun xMessagesList(model: ChatModel, allMessages: List<Message>) {
    allMessages
        .groupBy { it.timestamp.date }
        .entries
        .sortedBy { it.key }
        .forEach { (date, messages) ->
            xDateLabel(date)

            messages.sortedBy { it.timestamp }.forEach {
                xMessage(it)
            }
        }
    flex(
        attrs = {
            ref { el ->
                val newMessage = model.newMessageEvent.subscribe {
                    ensureScrollTo(el, ScrollBehavior.SMOOTH)
                }

                val newChat = model.selectedChat.subscribe {
                    ensureScrollTo(el, ScrollBehavior.SMOOTH)
                }

                onDispose {
                    model.newMessageEvent.unsubscribe(newMessage)
                    model.selectedChat.unsubscribe(newChat)
                }
            }
        }
    )
}

@Composable
private fun xDateLabel(date: LocalDate) {
    val theme = XTheme.current

    horizontal(
        styleBuilder = {
            height(32.px)

            width(100.percent)

            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)

            color(theme.primaryColor)

            applyCustomFont(font = JetBrainsMono.Regular, size = FontSize.Small)
        }
    ) {
        if (date == today()) {
            Text("Today")
        } else {
            Text("${date.dayOfMonth} ${date.month.name.lowercase().capitalized()} ${date.year}")
        }
    }
}

@Composable
private fun xMessage(message: Message) {
    horizontal(
        styleBuilder = {
            margin(5.px, 16.px, 5.px, 16.px)
        }
    ) {
        val theme = XTheme.current

        xLogo(30.px, message.author.name, take = 2) {
            if (message.author is Author.Me) {
                color(theme.secondaryTextColor)
            }
        }

        gap(16.px)

        vertical(
            styleBuilder = {
                flex(1, 0, auto.unsafeCast<CSSNumeric>())

                width(0.percent)
            }
        ) {

            horizontal(
                styleBuilder = {
                    justifyContent(JustifyContent.SpaceBetween)

                    applyCustomFont(size = FontSize.Small)
                }
            ) {
                Span(
                    attrs = {
                        style {
                            applyCustomFont(font = JetBrainsMono.Bold)

                            if (message.author is Author.Me) {
                                color(theme.secondaryTextColor)
                            }
                        }
                    }
                ) {
                    Text(message.author.name)
                }

                horizontal {
                    if (message.author is Author.Me) {
                        xMessageStatus(message) {
                            gap(8.px)
                        }
                    }

                    Span {
                        Text("${message.timestamp.time}")
                    }
                }
            }

            horizontal(
                styleBuilder = {
                    color(theme.secondaryTextColor)

                    property("overflow-wrap", "anywhere")
                }
            ) {
                Text(message.text)
            }
        }
    }
}
