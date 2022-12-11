package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.base.components.*
import com.example.secure.chat.web.font.FontSize
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.font.fonts.JetBrainsMono
import com.example.secure.chat.web.model.ChatModel
import com.example.secure.chat.web.model.chat.Chat
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.MessageStatus
import com.example.secure.chat.web.model.chat.processors.securityManagerBot
import com.example.secure.chat.web.theme.DarkTheme
import com.example.secure.chat.web.theme.XTheme
import com.example.secure.chat.web.utils.consts.CHAT_LIST_ITEM_HEIGHT
import com.example.secure.chat.web.utils.consts.CHAT_LIST_WIDTH
import com.example.secure.chat.web.utils.consts.SECRET_PLACEHOLDER
import com.example.secure.chat.web.utils.now
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

object ChatItemStylesheet : StyleSheet() {
    val item by style {
        height(CHAT_LIST_ITEM_HEIGHT) // this is the way
        width(100.percent - 20.px) // 20 for padding 10 + 10

        padding(10.px) // yes, this padding

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
            width(CHAT_LIST_WIDTH)
        }
    ) {
        Style(ChatItemStylesheet)

        xChatItem(model, Chat.Local)

        xHorizontalSeparator()

        val chats by remember { model.chats.asState() }

        // todo virtual lists?, better ordering?
        chats.values.sortedByDescending { it.lastMessage.value?.timestamp }.forEach { chat ->
            xChatItem(model, chat)

            xHorizontalSeparator()
        }
    }
}

@Composable
private fun xChatItem(model: ChatModel, chat: Chat) {
    val theme = XTheme.current

    val selectedChatState by remember { model.selectedChat.asState() }

    val locked by remember { chat.isLocked.asState() }

    val lastMessage by remember { chat.lastMessage.asState() }

    vertical(
        attrs = {
            style {
                if (chat == selectedChatState) {
                    backgroundColor(theme.secondaryColor)
                }

                if (locked) {
                    color(theme.secondaryColor)
                    backgroundColor(theme.secondaryColor30)
                }
            }

            classes(ChatItemStylesheet.item)

            onClick {
                if (!locked) {
                    if (selectedChatState != chat) {
                        model.selectedChat.value = chat
                    }
                }
            }
        },
    ) {
        horizontal(
            styleBuilder = {
                height(100.percent)

                gap(16.px) // good layout

                alignItems(AlignItems.Center)
            }
        ) {
            val name = when (chat) {
                is Chat.Global -> chat.name
                is Chat.Local -> securityManagerBot.name
            }

            val lastMessageStatus by remember {
                lastMessage?.status?.asState()
                    ?: mutableStateOf(MessageStatus.Verified) // no message - no difference
            }

            // good layout
            xLogo(40.px, name, withOutline = !locked && lastMessageStatus == MessageStatus.Unread)

            xChatDescription(chat, locked, lastMessage)
        }
    }
}


@Composable
private fun xChatDescription(chat: Chat, locked: Boolean, lastMessage: Message?) {
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
                            Text(securityManagerBot.name)
                        }
                    }

                    is Chat.Global -> {
                        val name = (if (locked) "#${chat.id} " else "") + chat.name

                        xEllipsis(name, maxSymbols = if (locked) 40 - chat.id.toString().length else 16) {
                            applyCustomFont(font = JetBrainsMono.Bold)
                        }
                    }
                }

                if (!locked) {
                    lastMessage?.let { message ->
                        horizontal(
                            styleBuilder = {
                                applyCustomFont(size = FontSize.Small)
                            }
                        ) {
                            xMessageStatus(message) {
                                gap(8.px) // just gap, big enough
                            }

                            Span {
                                val date = message.timestamp
                                if (date.date == now().date) {
                                    Text("${date.time}")
                                } else {
                                    Text("${date.date}")
                                }
                            }
                        }
                    }
                }
            }

            val theme = XTheme.current

            if (!locked) {
                lastMessage?.let { lastMessage ->
                    xEllipsis(lastMessage.author.name) {
                        applyCustomFont(size = FontSize.Small)
                    }

                    val text = when {
                        lastMessage.isSecret -> SECRET_PLACEHOLDER
                        else -> lastMessage.text
                    }

                    xEllipsis(text) {
                        color(theme.secondaryTextColor)
                        applyCustomFont(size = FontSize.Small)
                    }
                }
            }
        }
    }
}
