package org.jetbrains.kotlinconf

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        alwaysOnTop = true,
        state = rememberWindowState(width = 600.dp, height = 800.dp),
    ) {
        App(ApplicationContext())
    }
}
