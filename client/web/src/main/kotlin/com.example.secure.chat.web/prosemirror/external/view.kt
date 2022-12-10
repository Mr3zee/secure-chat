@file:JsModule("prosemirror-view")
@file:JsNonModule

package com.example.secure.chat.web.prosemirror.external

import org.w3c.dom.HTMLElement
import kotlin.js.*

external class EditorView(place: DOMNode?, props: DirectEditorProps) {
    val dom: HTMLElement
    val state: EditorState

    fun updateState(state: EditorState)

    fun destroy()

    fun dispatch(tr: Transaction)

    fun focus()
}

external interface EditorProps {
    var editable: ((state: EditorState) -> Boolean)?
}


external interface DirectEditorProps : EditorProps {
    var state: EditorState
    var dispatchTransaction: ((tr: Transaction) -> Unit)?
}

external class DecorationSet {
    companion object {
        fun create(doc: Node, decorations: Array<Decoration>): DecorationSet
    }
}

external class Decoration {
    companion object {
        fun widget(pos: Number, domNode: org.w3c.dom.Node): Decoration
    }
}
