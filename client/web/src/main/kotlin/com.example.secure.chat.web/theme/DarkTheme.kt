package com.example.secure.chat.web.theme

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb

object DarkTheme : Theme {
    override val primaryColor: CSSColorValue = rgb(64, 88, 100)

    override val textColor: CSSColorValue = rgb(147, 229, 130)

    override val secondaryTextColor: CSSColorValue = rgb(145, 186, 247)

    override val backgroundColor: CSSColorValue = rgb(24, 28, 37)
}
