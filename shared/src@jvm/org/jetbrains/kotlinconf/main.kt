package org.jetbrains.kotlinconf

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ObservableSettings
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.kotlinconf.storage.createSettings
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.dsl.module

private val platformModule = module {
    single<ObservableSettings> { createSettings() }
    single<LocalNotificationService> { EmptyLocalNotificationService() }
    single<NotificationPlatformConfiguration> {
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = false,
            notificationIconPath = null,
        )
    }
}

class JvmLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        println("[$tag] ${lazyMessage()}")
    }
}

fun main() {
    initApp(JvmLogger(), platformModule, Flags(supportsNotifications = false))

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
