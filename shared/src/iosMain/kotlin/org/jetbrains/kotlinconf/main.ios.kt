package org.jetbrains.kotlinconf

import androidx.compose.ui.window.ComposeUIViewController
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import org.jetbrains.kotlinconf.utils.IOSLogger
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController

private val platformModule = module {
    single<LocalNotificationService> { IOSLocalNotificationService(get(), get()) }
    single<ObservableSettings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
    single<Logger> { IOSLogger() }
    single<NotificationPlatformConfiguration> {
        NotificationPlatformConfiguration.Ios(
            showPushNotification = true,
            askNotificationPermissionOnStart = false,
            notificationSoundName = null,
        )
    }
}

@Suppress("unused") // Called from Swift
fun initApp() = initApp(platformModule)

@Suppress("unused") // Called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}
