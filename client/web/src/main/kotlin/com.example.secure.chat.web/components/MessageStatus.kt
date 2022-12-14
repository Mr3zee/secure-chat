package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.base.types.Content
import com.example.secure.chat.web.model.chat.Message
import com.example.secure.chat.web.model.chat.MessageStatus
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun xMessageStatus(message: Message, displayLocal: Boolean = false, content: Content = {}) {
    val theme = XTheme.current

    val status by remember { message.status.asState() }

    val text = when {
        status == MessageStatus.Pending -> "Pending" to theme.warningColor
        status == MessageStatus.Failed -> "Failed" to theme.errorColor
        displayLocal && status == MessageStatus.Local -> "Local" to theme.secondaryTextColor60
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
