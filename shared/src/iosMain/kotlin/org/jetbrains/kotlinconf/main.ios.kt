package org.jetbrains.kotlinconf

import moe.tlaster.precompose.PreComposeApplication
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = PreComposeApplication {
    App(ApplicationContext())
}