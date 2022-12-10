package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.base.components.*
import com.example.secure.chat.web.models.ChatModel
import com.example.secure.chat.web.models.TextInputType
import com.example.secure.chat.web.theme.DarkTheme
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

object ChatInputStyleSheet : StyleSheet() {
    val button by style {
        height(100.percent)

        paddingLeft(24.px) // this is the way
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
        }
    ) {
        val inputType by remember { model.inputType.asState() }

        Style(ChatInputStyleSheet)

        vertical(
            styleBuilder = {
                flex(1, 0, auto.unsafeCast<CSSNumeric>()) // not magic, learn css flex-grow

                margin(10.px, 16.px, 10.px, 16.px) //  this input margin, it just looks good with these values, no magic

                width(0.percent) // css hack
            },
        ) {
            when (inputType) {
                TextInputType.Message -> xInputField(
                    property = model.currentInput,
                    resetProperty = model.resetInput,
                    placeholder = "Write a message..."
                ) { model.submitMessage() }

                TextInputType.Secret -> xSecretInputField(
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