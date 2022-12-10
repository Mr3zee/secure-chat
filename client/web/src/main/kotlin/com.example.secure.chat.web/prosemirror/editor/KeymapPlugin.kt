package com.example.secure.chat.web.prosemirror.editor

import androidx.compose.runtime.NoLiveLiterals
import com.example.secure.chat.web.prosemirror.external.*
import kotlinx.js.jso

@NoLiveLiterals
fun keymapPlugin(onEnter: () -> Unit): Plugin = Plugin(jso {
    key = PluginKey("enter-plugin")
    props = jso {
        val handler = keydownHandler(jso {
            val enterCmd: PmCommand = { _, _, _ ->
                onEnter()
                true
            }

            this["Enter"] = enterCmd
            this["Mod-z"] = PmHistory::undo
            this["Mod-Shift-z"] = PmHistory::redo
        })

        handleKeyDown = { view, event ->
            val res = handler(view, event)
            if (res) {
                event.preventDefault()
                event.stopPropagation()
            }
            res
        }
    }
})
