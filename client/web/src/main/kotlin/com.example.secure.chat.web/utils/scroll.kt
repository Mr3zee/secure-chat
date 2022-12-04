package com.example.secure.chat.web.utils

import js.core.jso
import org.w3c.dom.HTMLElement
import org.w3c.dom.INSTANT
import org.w3c.dom.ScrollBehavior
import web.timers.setTimeout

fun ensureScrollTo(element: HTMLElement, behavior: ScrollBehavior = ScrollBehavior.INSTANT) {
    setTimeout(callback = {
        element.scrollIntoView(
            arg = jso {
                this.behavior = behavior
            }
        )
    })
}
