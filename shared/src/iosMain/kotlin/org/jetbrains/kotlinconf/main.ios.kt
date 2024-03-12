package org.jetbrains.kotlinconf

import androidx.compose.ui.window.ComposeUIViewController
import moe.tlaster.precompose.PreComposeApp
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    PreComposeApp {
        App(ApplicationContext())
    }
}
