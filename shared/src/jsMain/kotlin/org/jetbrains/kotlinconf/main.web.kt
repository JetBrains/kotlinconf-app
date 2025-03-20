package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.jetbrains.kotlinconf.ui.initCoil
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initCoil()
    initApp(platformModule)

    onWasmReady {
        ComposeViewport("ComposeApp") {
            App()
        }
    }
}
