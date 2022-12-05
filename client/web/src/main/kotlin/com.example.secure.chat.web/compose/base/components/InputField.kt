package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.secure.chat.web.compose.MutableProperty
import com.example.secure.chat.web.font.applyCustomFont
import com.example.secure.chat.web.prosemirror.editor.messageEditorView
import com.example.secure.chat.web.prosemirror.external.Transaction
import com.example.secure.chat.web.prosemirror.external.safeFocus
import com.example.secure.chat.web.theme.DarkTheme
import com.example.secure.chat.web.theme.XTheme
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.events.SyntheticKeyboardEvent


object ProsemirrorStyleSheet : StyleSheet() {
    val prosemirror by style {
        ".ProseMirror" {
            width(0.px)

            flex(1, 0, auto.unsafeCast<CSSNumeric>())

            whiteSpace("pre-wrap")
            property("overflow-wrap", "anywhere")
        }

        ".ProseMirror-focused" {
            border(0.px)
            outline("none")
        }

        ".ProseMirror-widget" {
            color(DarkTheme.primaryColor) //placeholder
        }
    }
}

@Composable
fun xInputField(
    property: MutableProperty<String>,
    resetProperty: MutableProperty<String>,
    placeholder: String = "",
    onSubmit: () -> Unit = {}
) {
    flex(
        styleBuilder = {
            height(100.percent)
        },
    ) {
        Style(ProsemirrorStyleSheet)

        val theme = XTheme.current

        flex(
            styleBuilder = {
                height(100.percent)
                width(100.percent)

                color(theme.secondaryTextColor)
            },
            attrs = {
                classes(ProsemirrorStyleSheet.prosemirror)

                ref { el ->
                    val view = messageEditorView(el, property, placeholder, onSubmit)
                    view.safeFocus()

                    val id = resetProperty.subscribe {
                        val tr = if (it.isNotEmpty()) {
                            view.state.tr.replaceWith(
                                from = 0,
                                to = view.state.doc.nodeSize - 2,
                                content = view.state.schema.text(it)
                            ) as Transaction
                        } else {
                            view.state.tr.delete(
                                from = 0,
                                to = view.state.doc.nodeSize - 2,
                            ) as Transaction
                        }

                        val newState = view.state.apply(tr)
                        view.updateState(newState)

                        view.safeFocus()
                    }

                    onDispose {
                        resetProperty.unsubscribe(id)
                        view.destroy()
                    }
                }
            }
        )
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
            outline("none")

            backgroundColor(Color.transparent)

            applyCustomFont()

            color(theme.textColor)
        }

        placeholder(placeholder)

        onInput {
            property.value = it.value
        }

        onEnter { onSubmit() }
    }
}

private fun AttrsScope<*>.onEnter(withShift: Boolean = false, handle: (SyntheticKeyboardEvent) -> Unit) {
    onKeyDown { event ->
        val acceptShift = (withShift && event.shiftKey) || (!withShift && !event.shiftKey)
        if (acceptShift && event.key == "Enter") {
            handle(event)
        }
    }
}
