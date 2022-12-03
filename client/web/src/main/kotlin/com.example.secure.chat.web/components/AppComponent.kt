package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.vertical
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.models.ChatModel
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.*

val HEADER_SIZE = 50.px

@Composable
fun AppComponent(
    model: ChatModel,
) {
    val theme = XTheme.current

    vertical(
        styleBuilder = {
            width(100.percent)
            height(100.percent)

            backgroundColor(theme.backgroundColor)

            applyCustomFont()

            color(theme.textColor)
        },
    ) {
        xAppHeader()

        xMainScreen(model)
    }
}
