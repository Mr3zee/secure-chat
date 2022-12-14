package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.DivAttrs
import com.example.secure.chat.web.compose.base.types.DivContent
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.dom.Div

@Composable
fun flex(styleBuilder: StyleBuilder = {}, attrs: DivAttrs = {}, content: DivContent = {}) {
    Div(
        attrs = {
            style {
                styleBuilder()

                display(DisplayStyle.Flex)
            }

            attrs()
        },
        content = content
    )
}


@Composable
fun horizontal(styleBuilder: StyleBuilder = {}, attrs: DivAttrs = {}, content: DivContent = {}) {
    flex(styleBuilder = {
        styleBuilder()
        flexDirection(FlexDirection.Row)
    }, attrs, content)
}

@Composable
fun vertical(styleBuilder: StyleBuilder = {}, attrs: DivAttrs = {}, content: DivContent = {}) {
    flex(styleBuilder = {
        styleBuilder()
        flexDirection(FlexDirection.Column)
    }, attrs, content)
}
