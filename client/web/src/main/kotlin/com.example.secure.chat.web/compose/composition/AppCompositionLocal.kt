package com.example.secure.chat.web.compose.composition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

/**
 * Uncomment and provide implementations when needed
 *
 * You will see 'CompositionLocal <***> not present' error in the console if something is missing here
 */
@Composable
fun AppCompositionLocalProvider(owner: AppOwner, content: @Composable () -> Unit) {
    CompositionLocalProvider(
//        LocalAccessibilityManager provides owner.accessibilityManager,
//        LocalAutofill provides owner.autofill,
//        LocalAutofillTree provides owner.autofillTree,
//        LocalClipboardManager provides owner.clipboardManager,
        LocalDensity provides owner.density,
//        LocalFocusManager provides owner.focusManager,
//        @Suppress("DEPRECATION")
//        LocalFontLoader providesDefault owner.fontLoader,
//        LocalFontFamilyResolver providesDefault owner.fontFamilyResolver,
//        LocalHapticFeedback provides owner.hapticFeedBack,
//        LocalInputModeManager provides owner.inputModeManager,
        LocalLayoutDirection provides owner.layoutDirection,
//        LocalTextInputService provides owner.textInputService,
//        LocalTextToolbar provides owner.textToolbar,
//        LocalUriHandler provides owner.uriHandler,
//        LocalViewConfiguration provides owner.viewConfiguration,
//        LocalWindowInfo provides owner.windowInfo,
//        LocalPointerIconService provides owner.pointerIconService,
        content = content
    )
}
