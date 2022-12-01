@file:Suppress("unused")

package com.example.secure.chat.web.font.fonts

import androidx.compose.ui.text.font.FontStyle
import com.example.secure.chat.web.font.BaseFont
import org.jetbrains.skia.FontWeight

sealed class JetBrainsMono : BaseFont {
    override val fontFamily: String = "JetBrains Mono"

    object Bold : JetBrainsMono() {
        override val fontWeight: Int = FontWeight.BOLD
        override val fontStyle: FontStyle = FontStyle.Normal
    }

    object Regular : JetBrainsMono() {
        override val fontWeight: Int = FontWeight.NORMAL
        override val fontStyle: FontStyle = FontStyle.Normal
    }

    object Thin : JetBrainsMono() {
        override val fontWeight: Int = FontWeight.THIN
        override val fontStyle: FontStyle = FontStyle.Normal
    }
}
