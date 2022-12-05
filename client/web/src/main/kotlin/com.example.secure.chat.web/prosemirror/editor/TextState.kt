package com.example.secure.chat.web.prosemirror.editor

import com.example.secure.chat.web.prosemirror.external.*
import kotlinx.js.jso
import kotlin.js.Json

fun textState(
    text: String,
    placeholder: String,
    onEnter: () -> Unit
): EditorState {
    return EditorState.create(jso {
        schema = baseSchema
        doc = Node.fromJSON(baseSchema, docFromText(text))

        plugins = arrayOf(
            PmHistory.history(),
            keymapPlugin(onEnter),
            placeholderPlugin(placeholder),
            keymap(baseKeymap),
        )
    })
}

fun docFromText(text: String): Json {
    val string = when {
        text.isEmpty() -> """
            {
                "type": "doc",
                "content": []
            }
        """.trimIndent()
        else -> """
            {
                "type": "doc",
                "content": [
                    {
                        "type": "text",
                        "text": "$text"
                    }
                ]
            }
        """.trimIndent()
    }

    return JSON.parse(string)
}
