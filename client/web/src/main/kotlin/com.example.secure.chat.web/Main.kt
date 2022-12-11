package com.example.secure.chat.web

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.example.secure.chat.platform.Ui
import com.example.secure.chat.platform.launch
import com.example.secure.chat.web.components.AppComponent
import com.example.secure.chat.web.crypto.*
import com.example.secure.chat.web.model.ChatModel
import com.example.secure.chat.web.model.api.ChatApiStub
import com.example.secure.chat.web.model.coder.DefaultCoder
import com.example.secure.chat.web.model.coder.TestCoder
import com.example.secure.chat.web.model.creds.Credentials
import com.example.secure.chat.web.theme.provideTheme
import com.example.secure.chat.web.utils.consts.ROOT_ELEMENT
import com.example.secure.chat.web.utils.consts.VISIBILITY_CHANGE_EVENT_NAME
import com.example.secure.chat.web.utils.consts.VISIBILITY_CHANGE_VISIBLE
import com.example.secure.chat.web.utils.isDevEnv
import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.Document


fun main() {
    launch(Ui) {
        val (_, sk) = crypto.genRsaKeyPair()
        val exported = crypto.exportPrivateRSAKeyPEM(sk)
        console.log(exported)
    }

    val lifecycle = LifecycleRegistry()

    val coder = when {
        isDevEnv() -> TestCoder
        else -> DefaultCoder
    }

    val credentials = Credentials()

    val rootModel = ChatModel(
        credentials = credentials,
        coder = coder,
        api = ChatApiStub,
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
