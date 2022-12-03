package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.base.components.*
import com.example.secure.chat.web.models.ChatModel
import com.example.secure.chat.web.theme.DarkTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

object ChatInputStyleSheet : StyleSheet() {
    val button by style {
        height(100.percent)

        paddingLeft(24.px)
        paddingRight(24.px)

        border(0.px)
        outlineWidth(0.px)

        backgroundColor(Color.transparent)

        color(DarkTheme.textColor)

        self + hover style {
            backgroundColor(DarkTheme.primaryColor)
        }
    }
}

@Composable
fun xChatInput(model: ChatModel) {
    horizontal(
        styleBuilder = {
            width(100.percent)
            minHeight(40.px) // 20 input margin + 20 one-line text
        }
    ) {
        val inputType by remember { model.inputType.asState() }

        Style(ChatInputStyleSheet)

        vertical(
            styleBuilder = {
                flex(1, 0, auto.unsafeCast<CSSNumeric>())

                margin(10.px, 16.px, 10.px, 16.px)
            },
        ) {
            when (inputType) {
                TextInputType.Message -> InputField(
                    property = model.currentInput,
                    placeholder = "Write a message..."
                ) { model.submitMessage() }

                TextInputType.Secret -> SecretInputField(
                    property = model.currentInput,
                    placeholder = "Write your secret..."
                ) { model.submitMessage() }
            }
        }

        xVerticalSeparator()

        Button(
            attrs = {
                classes(ChatInputStyleSheet.button)

                onClick {
                    model.submitMessage()
                }
            }
        ) {
            Text("Send")
        }
    }
}

enum class TextInputType {
    Message, Secret
}
