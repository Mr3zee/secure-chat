package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.*
import com.example.secure.chat.web.font.FontSize
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.models.ChatModel
import com.example.secure.chat.web.models.chat.Chat
import com.example.secure.chat.web.theme.DarkTheme
import com.example.secure.chat.web.theme.XTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
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

        model.chats.value.forEach { chat ->
            xChatItem(chat)

            xHorizontalSeparator()
        }
    }
}

@Composable
private fun xChatItem(chat: Chat) {
    vertical(
        attrs = {
            classes(ChatItemStylesheet.item)
        },
    ) {
        horizontal(
            styleBuilder = {
                height(100.percent)

                gap(16.px)

                alignItems(AlignItems.Center)
            }
        ) {
            xChatLogo(chat)

            xChatDescription(chat)
        }
    }
}


@Composable
private fun xChatDescription(chat: Chat) {
    horizontal(
        styleBuilder = {
            maxWidth(100.percent - 76.px)

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
                        Span {
                            Text("Local Security Manager")
                        }
                    }

                    is Chat.Global -> {
                        xEllipsis(chat.dto.name)
                    }
                }

                chat.lastMessage.value?.let { message ->
                    Span(
                        attrs = {
                            style {
                                applyCustomFont(size = FontSize.Small)
                            }
                        }
                    ) {
                        val date = message.timestamp
                        if (date.date == Clock.System.now().toLocalDateTime(TimeZone.UTC).date) {
                            Text("${date.time}")
                        } else {
                            Text("${date.date}")
                        }
                    }
                }
            }

            if (chat is Chat.Global) {
                val theme = XTheme.current

                chat.lastMessage.value?.let { lastMessage ->
                    xEllipsis(
                        content = lastMessage.text,
                        styleBuilder = {
                            color(theme.secondaryTextColor)
                            applyCustomFont(size = FontSize.Small)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun xChatLogo(chat: Chat) {
    val theme = XTheme.current

    Div(
        attrs = {
            style {
                width(40.px)
                height(40.px)

                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)

                borderRadius(50.percent)

                backgroundColor(theme.secondaryColor)
            }
        }
    ) {
        Span {
            val logo = when (chat) {
                is Chat.Global -> chat.dto.name.firstOrNull()?.toString() ?: return@Span
                is Chat.Local -> "LSM"
            }

            Text(logo)
        }
    }
}
