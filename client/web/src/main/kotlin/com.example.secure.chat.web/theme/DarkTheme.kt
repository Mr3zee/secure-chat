package com.example.secure.chat.web.theme

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.rgba

object DarkTheme : Theme {
    override val primaryColor: CSSColorValue = rgb(64, 88, 100)

    override val primaryColor60: CSSColorValue = rgba(64, 88, 100, 0.6)

    override val secondaryColor: CSSColorValue = rgba(64, 88, 100, 0.6)

    override val secondaryColor30: CSSColorValue = rgba(64, 88, 100, 0.3)

    override val textColor: CSSColorValue = rgb(147, 229, 130)

    override val secondaryTextColor: CSSColorValue = rgb(145, 186, 247)

    override val secondaryTextColor60: CSSColorValue = rgba(145, 186, 247, 0.6)

    override val backgroundColor: CSSColorValue = rgb(24, 28, 37)

    override val warningColor: CSSColorValue = rgb(239, 160, 11) // 214, 81, 8

    override val errorColor: CSSColorValue = rgb(215, 38, 56)
}
