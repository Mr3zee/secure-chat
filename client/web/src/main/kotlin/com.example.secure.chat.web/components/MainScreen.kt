package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.horizontal
import com.example.secure.chat.web.compose.base.components.xVerticalSeparator
import com.example.secure.chat.web.models.ChatModel
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width

@Composable
fun xMainScreen(model: ChatModel) {
    horizontal(
        styleBuilder = {
            width(100.percent)
            height(100.percent - HEADER_SIZE)
        }
    ) {
        xChatList(model)

        xVerticalSeparator()

        xChatWindow(model)
    }
}
