@file:JsModule("prosemirror-keymap")
@file:JsNonModule

package com.example.secure.chat.web.prosemirror.external

import org.w3c.dom.events.KeyboardEvent

external fun keymap(bindings: dynamic): Plugin

external fun keydownHandler(bindings: dynamic): (EditorView, KeyboardEvent) -> Boolean
