package org.jetbrains.kotlinconf

import androidx.compose.ui.window.ComposeUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController

private val platformModule = module {
    single<NotificationService> { IOSNotificationService(get()) }
    single<ObservableSettings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
}

fun MainViewController(): UIViewController = ComposeUIViewController {
    App(platformModule)
}
