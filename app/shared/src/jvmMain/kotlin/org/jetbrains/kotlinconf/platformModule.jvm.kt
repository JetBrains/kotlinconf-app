package org.jetbrains.kotlinconf

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ObservableSettings
import org.jetbrains.kotlinconf.storage.createSettings
import org.koin.dsl.module

val platformModule = module {
    single<ObservableSettings> { createSettings() }
    single<LocalNotificationService> { EmptyLocalNotificationService() }
    single<NotificationPlatformConfiguration> {
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = false,
            notificationIconPath = null,
        )
    }
}
