package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.Content
import com.example.secure.chat.web.models.chat.Message
import com.example.secure.chat.web.models.chat.MessageStatus
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun xMessageStatus(message: Message, content: Content = {}) {
    val theme = XTheme.current

    val text = when (message.status.value) {
        MessageStatus.Local -> "Pending" to theme.warningColor // todo retry
        MessageStatus.Failed -> "Failed" to theme.errorColor
        else -> null
    }

    text?.let { (text, color) ->
        Span(
            attrs = {
                style {
                    color(color)
                }
            }
        ) {
            Text(text)
        }

        content()
    }
}
