package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun xEllipsis(content: String, maxSymbols: Int = 40, styleBuilder: StyleBuilder = {}) {
    Span(
        attrs = {
            style {
                width(100.percent)

                whiteSpace("nowrap")
                overflow("hidden")

                styleBuilder()
            }
        }
    ) {
        Text(content.substring(0..maxSymbols - 3))

        if (content.length > maxSymbols - 3) {
            Span {
                Text("...")
            }
        }
    }
}
