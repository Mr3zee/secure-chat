package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.horizontal
import com.example.secure.chat.web.compose.base.components.vertical
import com.example.secure.chat.web.compose.base.components.xHorizontalSeparator
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun xAppHeader() {
    Header(
        attrs = {
            style {
                width(100.percent)
                height(HEADER_SIZE)

                overflowX("clip")
            }
        }
    ) {
        vertical(
            styleBuilder = {
                width(100.percent)
                height(100.percent)

                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
            }
        ) {
            horizontal(
                styleBuilder = {
                    fontSize(36.px)
                }
            ) {
                val theme = XTheme.current

                Span(
                    attrs = {
                        style {
                            color(theme.secondaryColor)
                        }
                    }
                ) {
                    Text("c2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQ=")
                }

                Span {
                    Text("secure.chat")
                }

                Span(
                    attrs = {
                        style {
                            color(theme.secondaryColor)
                        }
                    }
                ) {
                    Text("c2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQuc2VjdXJlLmNoYXQ=")
                }
            }
        }

        xHorizontalSeparator()
    }
}
