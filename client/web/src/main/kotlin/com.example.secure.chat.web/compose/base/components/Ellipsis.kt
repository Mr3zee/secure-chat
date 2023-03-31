package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun xEllipsis(content: String, maxSymbols: Int = 35, styleBuilder: StyleBuilder = {}) {
    Span(
        attrs = {
            style {
                whiteSpace("nowrap")
                overflow("hidden")

                styleBuilder()
            }
        }
    ) {
        val text = if (content.length > maxSymbols) {
            content.dropLast(maxOf(0, minOf(content.length - maxSymbols - 3, content.length)))
        } else content

        Text(text.trim())

        if (content.length > maxSymbols) {
            Span {
                Text("...")
            }
        }
    }
}
