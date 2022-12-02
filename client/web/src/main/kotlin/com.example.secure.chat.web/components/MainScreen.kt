package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.horizontal
import com.example.secure.chat.web.compose.base.components.xVerticalSeparator
import com.example.secure.chat.web.models.ChatModel
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.keywords.auto

@Composable
fun xMainScreen(model: ChatModel) {
    horizontal(
        styleBuilder = {
            flex(1, 0, auto.unsafeCast<CSSNumeric>())
        }
    ) {
        xChatList(model)

        xVerticalSeparator()

        xChatTimeline(model)
    }
}
