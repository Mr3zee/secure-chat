package com.example.secure.chat.web

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.example.secure.chat.web.components.AppComponent
import com.example.secure.chat.web.model.ChatModel
import com.example.secure.chat.web.model.api.ChatApiImpl
import com.example.secure.chat.web.model.coder.DefaultCoder
import com.example.secure.chat.web.model.creds.Credentials
import com.example.secure.chat.web.theme.provideTheme
import com.example.secure.chat.web.utils.consts.ROOT_ELEMENT
import com.example.secure.chat.web.utils.consts.VISIBILITY_CHANGE_EVENT_NAME
import com.example.secure.chat.web.utils.consts.VISIBILITY_CHANGE_VISIBLE
import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Document


fun main() {
    val lifecycle = LifecycleRegistry()

    val credentials = Credentials()

    val rootModel = ChatModel(
        credentials = credentials,
        coder = DefaultCoder,
        api = ChatApiImpl,
        componentContext = DefaultComponentContext(lifecycle)
    )

    lifecycle.attachToDocument()

    renderComposable(rootElementId = ROOT_ELEMENT) {
        provideTheme {
            AppComponent(rootModel)
        }
    }
}

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (document.visibilityState == VISIBILITY_CHANGE_VISIBLE) {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    document.addEventListener(type = VISIBILITY_CHANGE_EVENT_NAME, callback = { onVisibilityChanged() })
}

private val Document.visibilityState: String
    get() = asDynamic().visibilityState.unsafeCast<String>()
