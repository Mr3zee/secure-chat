package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import com.example.secure.chat.web.models.chat.whitespaceRegex
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun xLogo(
    size: CSSNumeric,
    name: String,
    take: Int = 3,
    withOutline: Boolean = false,
    styleBuilder: StyleBuilder = {}
) {
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

                if (withOutline) {
                    border {
                        color = theme.secondaryTextColor
                        width = 2.px
                        style = LineStyle.Solid
                    }
                }

                boxSizing("border-box")

                backgroundColor(theme.secondaryColor)
            }
        }
    ) {
        Span(
            attrs = {
                style {
                    styleBuilder()
                }
            }
        ) {
            val logo = name
                .split(whitespaceRegex)
                .mapNotNull { it.firstOrNull() }
                .take(take)
                .joinToString("") { it.titlecase() }

            Text(logo)
        }
    }
}
