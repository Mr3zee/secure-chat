package com.example.secure.chat.web.prosemirror.external

typealias DOMNode = org.w3c.dom.Node

fun EditorView.safeFocus() {
    try {
        focus()
    } catch (e: dynamic) {
        val t = (e as? Throwable) ?: Exception(e?.toString())
        console.warn("failed to focus the editor view", t)
    }
}

typealias PmCommand = (state: EditorState, dispatch: (tr: Transaction) -> Unit, view: EditorView) -> Boolean
