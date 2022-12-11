package com.example.secure.chat.web.compose.base.styles

import com.example.secure.chat.web.theme.Theme
import org.jetbrains.compose.web.css.StyleScope
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color

fun StyleScope.applyLockedStyles(theme: Theme) {
    color(theme.secondaryColor)
    backgroundColor(theme.secondaryColor30)
}
