package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.jetbrains.kotlinconf.ui.components.GalleryApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow {
        GalleryApp()
    }
}
