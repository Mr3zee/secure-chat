package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.MutableProperty
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.theme.DarkTheme
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Text

@Composable
fun xInputField(
    property: MutableProperty<String>,
    placeholder: String = "",
    onSubmit: () -> Unit = {}
) {
    flex(
        styleBuilder = {
            height(100.percent)
        }
    ) {
        val theme = XTheme.current

        val state by remember { property.asState() }

        flex(
            styleBuilder = {
                height(100.percent)
                width(100.percent)

                border(0.px)
                outlineWidth(0.px)

                property("z-index", 10)

                color(theme.secondaryTextColor)
            },
            attrs = {
                ref { el ->
                    val id = property.subscribe {
                        el.textContent = it
                        el.focus()
                    }

                    onDispose {
                        property.unsubscribe(id)
                    }
                }

                contentEditable(true)

                addEventListener("input") {
                    val data = it.nativeEvent.target.asDynamic().textContent as String
                    property.value = data
                }

                onEnter(onSubmit)
            }
        )

        flex(
            styleBuilder = {
                position(Position.Absolute)
                color(theme.primaryColor)

                property("z-index", 5)
            }
        ) {
            if (state.isEmpty()) {
                Text(placeholder)
            }
        }
    }
}

object SecretInputFieldStylesheet : StyleSheet() {
    val inputSecret by style {
        self + selector("::placeholder") style { // Chrome, Firefox, Opera, Safari 10.1+
            color(DarkTheme.primaryColor)
        }
    }
}

@Composable
fun xSecretInputField(property: MutableProperty<String>, placeholder: String, onSubmit: () -> Unit = {}) {
    Style(SecretInputFieldStylesheet)

    val theme = XTheme.current

    val state by remember { property.asState() }

    Input(InputType.Password) {
        value(state)

        classes(SecretInputFieldStylesheet.inputSecret)

        style {
            padding(0.px)

            border(0.px)
            outlineWidth("0")

            backgroundColor(Color.transparent)

            applyCustomFont()

            color(theme.textColor)
        }

        placeholder(placeholder)

        onInput {
            property.value = it.value
        }

        onEnter(onSubmit)
    }
}

fun AttrsScope<*>.onEnter(handle: () -> Unit) {
    onKeyDown { event ->
        if (!event.shiftKey && event.key == "Enter") {
            handle()
        }
    }
}
