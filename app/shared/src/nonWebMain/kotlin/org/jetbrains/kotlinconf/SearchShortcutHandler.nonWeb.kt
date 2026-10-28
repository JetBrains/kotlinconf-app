package org.jetbrains.kotlinconf

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

@Composable
actual fun Modifier.searchShortcut(onTriggered: () -> Unit): Modifier {
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(hasFocus) {
        if (!hasFocus) {
            runCatching { focusRequester.requestFocus() }
        }
    }

    return this
        .onFocusChanged { hasFocus = it.hasFocus }
        .focusRequester(focusRequester)
        .focusable()
        .onPreviewKeyEvent { event ->
            val modifierPressed = if (isMacPlatform) {
                event.isMetaPressed && !event.isCtrlPressed
            } else {
                event.isCtrlPressed && !event.isMetaPressed
            }
            val isFindShortcut = event.type == KeyEventType.KeyDown
                    && event.key == Key.F
                    && modifierPressed
            if (isFindShortcut) {
                onTriggered()
                true
            } else {
                false
            }
        }
}
