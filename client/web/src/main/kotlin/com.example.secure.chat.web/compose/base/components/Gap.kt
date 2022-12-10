package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.width

@Composable
fun gap(size: CSSNumeric) = flex(styleBuilder = {
    width(size)
    height(size)
})
