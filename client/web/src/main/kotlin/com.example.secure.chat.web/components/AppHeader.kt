package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.components.vertical
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.Text

@Composable
fun AppHeader() {
    Header(
        attrs = {
            style {
                width(100.percent)
                height(40.px)
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
            Div(attrs = {
                style {
                    fontSize(32.px)
                }
            }) {
                Text("secure.chat")
            }
        }
    }
}
