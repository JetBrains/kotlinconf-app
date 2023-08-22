package org.jetbrains.kotlinconf

import moe.tlaster.precompose.PreComposeApplication
import org.jetbrains.kotlinconf.storage.ApplicationContext
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = PreComposeApplication("KotlinConf App") {
    App(ApplicationContext())
}