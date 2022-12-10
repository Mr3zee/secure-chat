package com.example.secure.chat.web.compose.base.components

import androidx.compose.runtime.Composable
import com.example.secure.chat.web.compose.base.types.HeadingAttrs
import com.example.secure.chat.web.compose.base.types.HeadingContent
import com.example.secure.chat.web.compose.base.types.StyleBuilder
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.px

@Composable
fun h1(
    styleBuilder: StyleBuilder = {},
    attrs: HeadingAttrs = {},
    content: HeadingContent = {},
) = org.jetbrains.compose.web.dom.H1(
    attrs = defaultAttrs(styleBuilder, attrs),
    content = content
)

@Composable
fun h2(
    styleBuilder: StyleBuilder = {},
    attrs: HeadingAttrs = {},
    content: HeadingContent = {},
) = org.jetbrains.compose.web.dom.H2(
    attrs = defaultAttrs(styleBuilder, attrs),
    content = content
)

@Composable
fun h3(
    styleBuilder: StyleBuilder = {},
    attrs: HeadingAttrs = {},
    content: HeadingContent = {},
) = org.jetbrains.compose.web.dom.H3(
    attrs = defaultAttrs(styleBuilder, attrs),
    content = content
)

@Composable
fun h4(
    styleBuilder: StyleBuilder = {},
    attrs: HeadingAttrs = {},
    content: HeadingContent = {},
) = org.jetbrains.compose.web.dom.H4(
    attrs = defaultAttrs(styleBuilder, attrs),
    content = content
)

@Composable
fun h5(
    styleBuilder: StyleBuilder = {},
    attrs: HeadingAttrs = {},
    content: HeadingContent = {},
) = org.jetbrains.compose.web.dom.H5(
    attrs = defaultAttrs(styleBuilder, attrs),
    content = content
)

@Composable
fun h6(
    styleBuilder: StyleBuilder = {},
    attrs: HeadingAttrs = {},
    content: HeadingContent = {},
) = org.jetbrains.compose.web.dom.H6(
    attrs = defaultAttrs(styleBuilder, attrs),
    content = content
)


private fun defaultAttrs(
    styleBuilder: StyleBuilder = {},
    attrs: HeadingAttrs = {},
): HeadingAttrs = {
    style {
        margin(0.px)

        styleBuilder()
    }

    attrs()
}