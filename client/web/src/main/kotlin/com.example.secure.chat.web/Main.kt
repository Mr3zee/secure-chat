package com.example.secure.chat.web

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.example.secure.chat.web.components.AppComponent
import com.example.secure.chat.web.compose.DefaultModelContext
import com.example.secure.chat.web.models.ChatModel
import com.example.secure.chat.web.models.api.ChatApiStub
import com.example.secure.chat.web.theme.withTheme
import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Document


fun main() {
    val lifecycle = LifecycleRegistry()

    val rootModel = ChatModel(ChatApiStub, modelContext = DefaultModelContext(lifecycle))

    lifecycle.attachToDocument()

    renderComposable(rootElementId = "root") {
        withTheme {
            AppComponent(rootModel)
        }
    }
}

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (document.visibilityState == "visible") {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    document.addEventListener(type = "visibilitychange", callback = { onVisibilityChanged() })
}

private val Document.visibilityState: String
    get() = asDynamic().visibilityState.unsafeCast<String>()
