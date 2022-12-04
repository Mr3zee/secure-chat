package com.example.secure.chat.web.theme

import org.jetbrains.compose.web.css.CSSColorValue

interface Theme {
    val primaryColor: CSSColorValue

    val primaryColor60: CSSColorValue

    val secondaryColor: CSSColorValue

    val textColor: CSSColorValue

    val secondaryTextColor: CSSColorValue

    val secondaryTextColor60: CSSColorValue

    val backgroundColor: CSSColorValue

    val warningColor: CSSColorValue

    val errorColor: CSSColorValue
}
