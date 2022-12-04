package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.base.components.*
import com.example.secure.chat.web.font.FontSize
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.font.fonts.JetBrainsMono
import com.example.secure.chat.web.models.ChatModel
import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.theme.DarkTheme
import com.example.secure.chat.web.theme.XTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

object ChatItemStylesheet : StyleSheet() {
    val item by style {
        height(60.px)
        width(100.percent - 20.px)

        padding(10.px)

        display(DisplayStyle.Flex)

        justifyContent(JustifyContent.Center)

        self + hover style {
            backgroundColor(DarkTheme.secondaryColor) // theme is static
        }
    }
}

@Composable
fun xChatList(model: ChatModel) {
    xScrollable(
        styleBuilder = {
            width(400.px)
        }
    ) {
        Style(ChatItemStylesheet)


        xChatItem(model, Chat.Local)

        xHorizontalSeparator()

        val chats by remember { model.chats.asState() }

        // todo virtual lists?, better ordering?
        chats.sortedByDescending { it.lastMessage.value?.timestamp }.forEach { chat ->
            xChatItem(model, chat)

            xHorizontalSeparator()
        }
    }
}

@Composable
private fun xChatItem(model: ChatModel, chat: Chat) {
    val theme = XTheme.current

    val selectedChatState by remember { model.selectedChat.asState() }

    vertical(
        attrs = {
            style {
                if (chat == selectedChatState) {
                    backgroundColor(theme.secondaryColor)
                }
            }

            classes(ChatItemStylesheet.item)

            onClick {
                model.selectedChat.value = chat
            }
        },
    ) {
        horizontal(
            styleBuilder = {
                height(100.percent)

                gap(16.px)

                alignItems(AlignItems.Center)
            }
        ) {
            val name = when (chat) {
                is Chat.Global -> chat.dto.name
                is Chat.Local -> "Local Security Manager"
            }

            xLogo(40.px, name)

            xChatDescription(chat)
        }
    }
}


@Composable
private fun xChatDescription(chat: Chat) {
    horizontal(
        styleBuilder = {
            flex(1, 0, auto.unsafeCast<CSSNumeric>())
        }
    ) {
        vertical(
            styleBuilder = {
                width(100.percent)
                height(100.percent)

                justifyContent(JustifyContent.Center)
            }
        ) {
            horizontal(
                styleBuilder = {
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Baseline)
                }
            ) {
                when (chat) {
                    is Chat.Local -> {
                        Span(
                            attrs = {
                                style {
                                    applyCustomFont(font = JetBrainsMono.Bold)
                                }
                            }
                        ) {
                            Text("Local Security Manager")
                        }
                    }

                    is Chat.Global -> {
                        xEllipsis(chat.dto.name) {
                            applyCustomFont(font = JetBrainsMono.Bold)
                        }
                    }
                }

                chat.lastMessage.value?.let { message ->
                    horizontal(
                        styleBuilder = {
                            applyCustomFont(size = FontSize.Small)
                        }
                    ) {
                        xMessageStatus(message) {
                            gap(8.px)
                        }

                        Span {
                            val date = message.timestamp
                            if (date.date == Clock.System.now().toLocalDateTime(TimeZone.UTC).date) {
                                Text("${date.time}")
                            } else {
                                Text("${date.date}")
                            }
                        }
                    }
                }
            }

            val theme = XTheme.current

            chat.lastMessage.value?.let { lastMessage ->
                xEllipsis(lastMessage.author.name) {
                    applyCustomFont(size = FontSize.Small)
                }

                xEllipsis(lastMessage.text) {
                    color(theme.secondaryTextColor)
                    applyCustomFont(size = FontSize.Small)
                }
            }
        }
    }
}
