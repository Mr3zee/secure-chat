package com.example.secure.chat.web.prosemirror.editor

import com.example.secure.chat.web.prosemirror.external.Schema
import kotlinx.js.jso

// https://prosemirror.net/examples/schema/
val baseSchema = Schema(jso {
    nodes = jso {
        text = jso()
        doc = jso {
            content = "text*"
        }
    }
})
