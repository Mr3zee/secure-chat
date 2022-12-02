package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.font.fonts.JetBrainsMono
import com.example.secure.chat.web.models.AppModel
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun AppComponent(
    model: AppModel,
) {
    val theme = XTheme.current

    Div(attrs = {
        style {
            width(100.percent)
            height(100.percent)

            backgroundColor(theme.backgroundColor)

            applyCustomFont(font = JetBrainsMono.Thin)

            color(theme.textColor)
        }
    }) {
        AppHeader()


    }
}
