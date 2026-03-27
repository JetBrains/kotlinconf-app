package org.jetbrains.kotlinconf.ui.theme

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.PlatformImeOptions

@OptIn(ExperimentalComposeUiApi::class)
actual fun keyboardOptions(): KeyboardOptions {
    return KeyboardOptions(platformImeOptions = PlatformImeOptions {
        usingNativeTextInput(true)
    })
}