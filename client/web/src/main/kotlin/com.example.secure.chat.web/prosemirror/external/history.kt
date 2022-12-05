package com.example.secure.chat.web.prosemirror.external

import kotlin.js.*

@JsNonModule
@JsModule("prosemirror-history")
external object PmHistory {
    interface HistoryOptions

    fun history(config: HistoryOptions = definedExternally): Plugin

    fun undo(state: EditorState, dispatch: ((tr: Transaction) -> Unit)? = definedExternally): Boolean

    fun redo(state: EditorState, dispatch: ((tr: Transaction) -> Unit)? = definedExternally): Boolean
}