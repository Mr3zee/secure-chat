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

@OptIn(ExperimentalComposeUiApi::class)
interface AppOwner {
    val accessibilityManager: AccessibilityManager?
    val autofill: Autofill?
    val autofillTree: AutofillTree
    val clipboardManager: ClipboardManager
    val density: Density
    val focusManager: FocusManager
    @Suppress("DEPRECATION")
    val fontLoader: FontLoader
    val fontFamilyResolver: FontFamily.Resolver
    val hapticFeedBack: HapticFeedback
    val inputModeManager: InputModeManager
    val layoutDirection: LayoutDirection
    val textInputService: TextInputService?
    val textToolbar: TextToolbar
    val uriHandler: UriHandler
    val viewConfiguration: ViewConfiguration
    val windowInfo: WindowInfo
}
