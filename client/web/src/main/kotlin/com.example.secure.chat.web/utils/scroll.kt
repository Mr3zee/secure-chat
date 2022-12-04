package com.example.secure.chat.web.utils

import kotlinx.js.jso
import kotlinx.js.timers.setTimeout
import org.w3c.dom.HTMLElement
import org.w3c.dom.INSTANT
import org.w3c.dom.ScrollBehavior

fun ensureScrollTo(element: HTMLElement, behavior: ScrollBehavior = ScrollBehavior.INSTANT) {
    setTimeout(callback = {
        element.scrollIntoView(
            arg = jso {
                this.behavior = behavior
            }
        )
    })
}
