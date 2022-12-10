@file:JsModule("prosemirror-model")
@file:JsNonModule

package com.example.secure.chat.web.prosemirror.external

import kotlin.js.*

external interface SchemaSpec {
    var nodes: dynamic /* Any | OrderedMap<NodeSpec> */

//    var marks: dynamic /* Any? | OrderedMap<MarkSpec>? */
}

external class Mark

external class Schema(spec: SchemaSpec) {
    fun text(text: String, marks: Array<Mark>? = definedExternally): Node
}

external class Node {
    val childCount: Int
    val firstChild: Node?
    val isTextblock: Boolean
    val content: Fragment
    val text: String
    val nodeSize: Int

    companion object {
        fun fromJSON(schema: Schema, json: Any): Node
    }
}

external class Fragment {
    val size: Int
}
