@file:JsModule("prosemirror-transform")
@file:JsNonModule

package com.example.secure.chat.web.prosemirror.external

import kotlin.js.*

open external class Transform(doc: Node) {
    fun replaceWith(from: Int, to: Int, content: Node): Transform

    fun delete(from: Number, to: Number): Transform
}
