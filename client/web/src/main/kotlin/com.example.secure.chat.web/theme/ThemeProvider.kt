package com.example.secure.chat.web.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.secure.chat.web.compose.base.types.Content

val XTheme = staticCompositionLocalOf<Theme> { DarkTheme }

@Composable
fun withTheme(theme: Theme = DarkTheme, content: Content = {}) {
    CompositionLocalProvider(
        XTheme providesDefault theme,
        content = content
    )
}
