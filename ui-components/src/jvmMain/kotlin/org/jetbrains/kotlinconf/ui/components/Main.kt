package org.jetbrains.kotlinconf.ui.components

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.kotlinconf.ui.GalleryApp

fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            alwaysOnTop = true,
            state = rememberWindowState(
                width = 800.dp, height = 600.dp,
            ),
            title = "Gallery",
        ) {
            DevelopmentEntryPoint {
                GalleryApp()
            }
        }
    }
}
