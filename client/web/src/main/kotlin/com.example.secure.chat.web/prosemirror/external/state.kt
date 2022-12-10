@file:JsModule("prosemirror-state")
@file:JsNonModule

package com.example.secure.chat.web.prosemirror.external

import org.w3c.dom.events.KeyboardEvent
import kotlin.js.*

external interface EditorStateConfig {
    var schema: Schema?
    var doc: Node?
    var plugins: Array<Plugin>?
}

external class EditorState {
    val doc: Node
    val tr: Transaction
    val schema: Schema

    fun apply(tr: Transaction): EditorState

    companion object {
        fun create(config: EditorStateConfig): EditorState
    }
}

external class PluginKey(name: String)

external class Plugin(spec: PluginSpec)

external interface PluginSpec {
    var key: PluginKey
    var props: PluginProps
}

external interface PluginProps {
    var handleKeyDown: (EditorView, KeyboardEvent) -> Boolean

    var decorations: ((state: EditorState) -> DecorationSet?)?
}

external class Transaction(doc: Node) : Transform
