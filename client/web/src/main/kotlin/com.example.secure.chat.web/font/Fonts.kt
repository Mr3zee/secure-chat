@file:Suppress("unused")

package com.example.secure.chat.web.font

import androidx.compose.ui.text.font.FontStyle
import com.example.secure.chat.web.font.fonts.JetBrainsMono
import org.jetbrains.compose.web.css.*

interface BaseFont{
    val fontFamily: String
    val fontWeight: Int
    val fontStyle: FontStyle
}

sealed interface FontSize {
    val value: CSSNumeric

    object Base: FontSize {
        override val value: CSSNumeric = 15.px
    }

    object Small: FontSize {
        override val value: CSSNumeric = 12.px
    }

    object Smaller: FontSize {
        override val value: CSSNumeric = 10.px
    }

    object Smallest: FontSize {
        override val value: CSSNumeric = 8.px
    }
}


fun StyleScope.applyCustomFont(
    size: FontSize = FontSize.Base,
    font: BaseFont = JetBrainsMono.Regular
) {
    fontFamily(font.fontFamily)
    fontStyle(font.fontStyle.toString())
    fontWeight(font.fontWeight)
    fontSize(size.value)
}
