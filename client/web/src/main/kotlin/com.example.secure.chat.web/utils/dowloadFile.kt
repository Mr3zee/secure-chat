package com.example.secure.chat.web.utils

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

fun downloadFile(filename: String, content: String) {
    val fake = document.createElement("a") as HTMLElement
    fake.setAttribute("href", "data:text/plain;charset=utf-8,${encodeURIComponent(content)}")
    fake.setAttribute("download", filename)

    fake.style.display = "none"
    document.body!!.appendChild(fake)

    fake.click()

    document.body!!.removeChild(fake)
}