package com.example.secure.chat.web.prosemirror.editor

import com.example.secure.chat.web.prosemirror.external.Decoration
import com.example.secure.chat.web.prosemirror.external.DecorationSet
import com.example.secure.chat.web.prosemirror.external.Plugin
import com.example.secure.chat.web.prosemirror.external.PluginKey
import kotlinx.browser.document
import kotlinx.js.jso

fun placeholderPlugin(placeholder: String): Plugin = Plugin(jso {
    key = PluginKey("placeholder-plugin")
    props = jso {
        decorations = { state ->
            val doc = state.doc
            if (doc.isTextblock && doc.content.size == 0) {
                DecorationSet.create(doc, arrayOf(Decoration.widget(0, document.createTextNode(placeholder))))
            } else null
        }
    }
})
