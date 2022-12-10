package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun xHorizontalSeparator(height: CSSNumeric = 1.px, styleBuilder: StyleBuilder = {}) {
    val theme = XTheme.current

    Div(
        attrs = {
            style {
                height(height)
                width(100.percent)

                backgroundColor(theme.secondaryColor)

                styleBuilder()
            }
        }
    )
}

@Composable
fun xVerticalSeparator(width: CSSNumeric = 1.px, styleBuilder: StyleBuilder = {}) {
    val theme = XTheme.current

    Div(
        attrs = {
            style {
                width(width)
                height(100.percent)

                backgroundColor(theme.secondaryColor)

                styleBuilder()
            }
        }
    )
}
