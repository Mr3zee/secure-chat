package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.DivAttrs
import com.example.secure.chat.web.compose.base.types.DivContent
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.px

@Composable
fun xScrollable(
    styleBuilder: StyleBuilder = {},
    innerStyleBuilder: StyleBuilder = {},
    attrs: DivAttrs = {},
    content: DivContent = {}
) {
    vertical(
        attrs = {

            style {
                minHeight(0.px)
                overflow("auto")

                styleBuilder()
            }
            attrs()
        },
    ) {
        vertical(attrs = { style { innerStyleBuilder() } }, content = content)
    }
}
