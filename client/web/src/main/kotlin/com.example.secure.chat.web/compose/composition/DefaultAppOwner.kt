package com.example.secure.chat.web.compose.composition

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.autofill.Autofill
import androidx.compose.ui.autofill.AutofillTree
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextInputService
import androidx.compose.ui.text.platform.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Provide implementations when needed, see [AppCompositionLocalProvider]
 */
@OptIn(ExperimentalComposeUiApi::class)
object DefaultAppOwner : AppOwner {
    override val density: Density = Density(1.0f)

    override val layoutDirection: LayoutDirection = LayoutDirection.Ltr

    override val fontFamilyResolver: FontFamily.Resolver
        get() = TODO("Not yet implemented")

    override val viewConfiguration: ViewConfiguration
        get() = TODO("Not yet implemented")
    override val accessibilityManager: AccessibilityManager
        get() = TODO("Not yet implemented")
    override val autofill: Autofill
        get() = TODO("Not yet implemented")
    override val autofillTree: AutofillTree
        get() = TODO("Not yet implemented")
    override val clipboardManager: ClipboardManager
        get() = TODO("Not yet implemented")
    override val focusManager: FocusManager
        get() = TODO("Not yet implemented")
    @Suppress("DEPRECATION")
    override val fontLoader: FontLoader
        get() = TODO("Not yet implemented")
    override val hapticFeedBack: HapticFeedback
        get() = TODO("Not yet implemented")
    override val inputModeManager: InputModeManager
        get() = TODO("Not yet implemented")
    override val textInputService: TextInputService
        get() = TODO("Not yet implemented")
    override val textToolbar: TextToolbar
        get() = TODO("Not yet implemented")
    override val uriHandler: UriHandler
        get() = TODO("Not yet implemented")
    override val windowInfo: WindowInfo
        get() = TODO("Not yet implemented")
}