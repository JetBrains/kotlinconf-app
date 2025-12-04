package org.jetbrains.kotlinconf

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.generated.resources.desktop.Res
import org.jetbrains.kotlinconf.generated.resources.desktop.app_name
import org.jetbrains.kotlinconf.utils.Logger

class JvmLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        println("[$tag] ${lazyMessage()}")
    }
}

fun main() {
    initApp(JvmLogger(), platformModule, Flags(supportsNotifications = false))

    System.setProperty("apple.awt.application.appearance", "system")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            alwaysOnTop = true,
            state = rememberWindowState(width = 600.dp, height = 800.dp),
        ) {
            App()
        }
    }
}
