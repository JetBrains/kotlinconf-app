package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.jetbrains.kotlinconf.ui.components.GalleryApp
import org.jetbrains.kotlinconf.ui.initCoil

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initCoil()
    ComposeViewport("ComposeApp") {
        GalleryApp()
    }
}
