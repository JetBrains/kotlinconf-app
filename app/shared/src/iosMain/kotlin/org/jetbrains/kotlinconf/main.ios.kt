package org.jetbrains.kotlinconf

import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.dsl.module
import platform.Foundation.NSLog
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController

private val platformModule = module {
    single<LocalNotificationService> { IOSLocalNotificationService(get(), get()) }
    single<ObservableSettings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
    single<NotificationPlatformConfiguration> {
        NotificationPlatformConfiguration.Ios(
            showPushNotification = true,
            askNotificationPermissionOnStart = false,
            notificationSoundName = null,
        )
    }
}

@Suppress("unused") // Called from Swift
fun initApp() = initApp(
    platformLogger = IOSLogger(),
    platformModule = platformModule,
    flags = Flags(
        enableBackOnMainScreens = false,
        rippleEnabled = false,
        hideKeyboardOnDrag = true,
    )
)

class IOSLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        NSLog("[$tag] ${lazyMessage()}")
    }
}

@Suppress("unused") // Called from Swift
fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
) {
    App()
}
