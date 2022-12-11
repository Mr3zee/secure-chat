package com.example.secure.chat.web.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.base.components.*
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import com.example.secure.chat.web.model.ChatInputType
import com.example.secure.chat.web.model.ChatModel
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

        if (inputType == ChatInputType.Copy) {
            vertical(
                styleBuilder = {
                    height(40.px) // input min width: 20px margin + 20px input

                    flex(1, 0, auto.unsafeCast<CSSNumeric>())
                }
            ) {
                xChatButton("Copy private key") {
                    model.copySecretToClipboard()
                }
            }
        } else {
            vertical(
                styleBuilder = {
                    flex(1, 0, auto.unsafeCast<CSSNumeric>()) // not magic, learn css flex-grow

                    //  this input margin, it just looks good with these values, no magic
                    margin(10.px, 16.px, 10.px, 16.px)

                    width(0.percent) // css hack
                },
            ) {
                when (inputType) {
                    ChatInputType.Message -> xInputField(
                        property = model.currentInput,
                        resetProperty = model.resetInput,
                        placeholder = "Write a message..."
                    ) { model.submitMessage() }

                    ChatInputType.Secret -> xSecretInputField(
                        property = model.currentInput,
                        placeholder = "Write your secret..."
                    ) { model.submitMessage() }

                    ChatInputType.File -> {}

                    else -> {} // unreachable
                }
            }

            xVerticalSeparator()

            when (inputType) {
                ChatInputType.Message, ChatInputType.Secret -> {
                    xChatButton("Send") {
                        model.submitMessage()
                    }
                }

                ChatInputType.File -> {
                    xChatButton("Cancel") {
                        model.cancelFileUpload()
                    }
                }

                else -> {} // unreachable
            }
        }
    }
}

@Composable
private fun xChatButton(text: String, styleBuilder: StyleBuilder = {}, doOnClick: () -> Unit) {
    Button(
        attrs = {
            style {
                styleBuilder()
            }

            classes(ChatInputStyleSheet.button)

            onClick {
                doOnClick()
            }
        }
    ) {
        Text(text)
    }
}
