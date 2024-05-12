package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import moe.tlaster.precompose.PreComposeApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow {
        PreComposeApp {
            App(ApplicationContext())
        }
    }
}