package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.vertical
import com.example.secure.chat.web.compose.base.components.xHorizontalSeparator
import com.example.secure.chat.web.models.ChatModel
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.keywords.auto

@Composable
fun xChatWindow(model: ChatModel) {
    vertical(
        styleBuilder = {
            flex(1, 0, auto.unsafeCast<CSSNumeric>())
        }
    ) {
        xChatTimeline(model)

        xHorizontalSeparator()

        xChatInput(model)
    }
}
