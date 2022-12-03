package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.models.chat.whitespaceRegex
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun xLogo(size: CSSNumeric, name: String) {
    val theme = XTheme.current

    Div(
        attrs = {
            style {
                width(size)
                height(size)

                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)

                borderRadius(50.percent)

                backgroundColor(theme.secondaryColor)
            }
        }
    ) {
        Span {
            val logo = name
                .split(whitespaceRegex)
                .mapNotNull { it.firstOrNull() }
                .take(3)
                .joinToString("") { it.titlecase() }

            Text(logo)
        }
    }
}
