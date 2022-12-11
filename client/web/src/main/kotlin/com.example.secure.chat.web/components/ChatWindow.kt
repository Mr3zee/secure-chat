package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.vertical
import com.example.secure.chat.web.compose.base.components.xHorizontalSeparator
import com.example.secure.chat.web.model.ChatModel
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width

@Composable
fun xChatWindow(model: ChatModel) {
    vertical(
        styleBuilder = {
            flex(1, 0, auto.unsafeCast<CSSNumeric>())

            width(0.percent) // css magic
        }
    ) {
        xChatTimeline(model)

        xHorizontalSeparator()

        xChatInput(model)
    }
}
