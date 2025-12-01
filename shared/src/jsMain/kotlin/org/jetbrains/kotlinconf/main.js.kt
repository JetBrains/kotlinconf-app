package org.jetbrains.kotlinconf

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import org.jetbrains.kotlinconf.ui.initCoil
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.skiko.wasm.onWasmReady
import org.w3c.dom.get

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initCoil()

    val supportsNotifications = window["supportsNotifications"] as? Boolean ?: false
    initApp(
        platformLogger = object : Logger {
            override fun log(tag: String, lazyMessage: () -> String) {
                console.log(tag, lazyMessage())
            }
        },
        platformModule = platformModule,
        flags = Flags(
            supportsNotifications = supportsNotifications
        ),
    )

    onWasmReady {
        ComposeViewport("ComposeApp") {
            App()
        }
    }
}
