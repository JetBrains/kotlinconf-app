package org.jetbrains.kotlinconf

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import moe.tlaster.precompose.PreComposeApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject",
    ) {
        PreComposeApp {
            App(ApplicationContext())
        }
    }
}
