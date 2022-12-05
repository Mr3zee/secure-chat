package com.example.secure.chat.web.prosemirror.editor

import com.example.secure.chat.web.compose.MutableProperty
import com.example.secure.chat.web.prosemirror.external.EditorState
import com.example.secure.chat.web.prosemirror.external.EditorView
import com.example.secure.chat.web.prosemirror.external.Transaction
import kotlinx.js.jso
import org.w3c.dom.HTMLElement
import kotlin.properties.Delegates

fun textView(
    node: HTMLElement,
    editorState: EditorState,
    dispatch: ((Transaction) -> Unit)? = null
): EditorView {
    return EditorView(node, jso {
        dispatchTransaction = dispatch
        state = editorState
    })
}

fun messageEditorView(
    mount: HTMLElement,
    property: MutableProperty<String>,
    placeholder: String,
    onEnter: () -> Unit
): EditorView {
    val state = textState(property.value, placeholder, onEnter = onEnter)
    var view by Delegates.notNull<EditorView>()
    view = textView(mount, state) {
        val newState = view.state.apply(it)
        property.value = newState.doc.firstChild?.text ?: ""
        view.updateState(newState)
    }
    return view
}
