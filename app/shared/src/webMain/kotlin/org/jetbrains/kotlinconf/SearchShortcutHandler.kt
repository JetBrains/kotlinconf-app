package org.jetbrains.kotlinconf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

// Web needs its own implementation, because to handle the key events on canvas it has to be focused,
// but it's not always the case.
// To be improved in Compose Multiplatform for web - https://youtrack.jetbrains.com/issue/CMP-9948
@Composable
actual fun Modifier.searchShortcut(onTriggered: () -> Unit): Modifier {
    val currentTrigger by rememberUpdatedState(onTriggered)
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { event ->
            val keyboardEvent = event as KeyboardEvent
            val modifierPressed = if (isMacPlatform) {
                keyboardEvent.metaKey && !keyboardEvent.ctrlKey
            } else {
                keyboardEvent.ctrlKey && !keyboardEvent.metaKey
            }
            val isFindShortcut = modifierPressed && (keyboardEvent.key == "f" || keyboardEvent.key == "F")
            if (isFindShortcut) {
                event.preventDefault()
                currentTrigger()
            }
        }
        document.addEventListener("keydown", listener)
        onDispose { document.removeEventListener("keydown", listener) }
    }
    return this
}
