package org.jetbrains.kotlinconf

import androidx.compose.ui.window.ComposeUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import org.jetbrains.kotlinconf.utils.IOSLogger
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController

private val platformModule = module {
    single<NotificationService> { IOSNotificationService(get(), get()) }
    single<ObservableSettings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
    single<Logger> { IOSLogger() }
}

fun MainViewController(): UIViewController = ComposeUIViewController {
    App(platformModule)
}
