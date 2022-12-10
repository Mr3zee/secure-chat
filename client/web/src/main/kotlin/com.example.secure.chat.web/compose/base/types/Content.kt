package com.example.secure.chat.web.compose.base.types

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLHeadingElement

typealias Content = @Composable () -> Unit

typealias DivContent = ContentBuilder<HTMLDivElement>

typealias HeadingContent = ContentBuilder<HTMLHeadingElement>
