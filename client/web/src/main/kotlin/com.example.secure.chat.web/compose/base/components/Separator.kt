package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun horizontalSeparator(height: CSSNumeric = 1.px) {
    val theme = XTheme.current

    Div(attrs = {
        style {
            height(height)
            width(100.percent)

            backgroundColor(theme.secondaryColor)
        }
    })
}

@Composable
fun verticalSeparator(width: CSSNumeric = 1.px) {
    val theme = XTheme.current

    Div(attrs = {
        style {
            width(width)
            height(100.percent)

            backgroundColor(theme.secondaryColor)
        }
    })
}
