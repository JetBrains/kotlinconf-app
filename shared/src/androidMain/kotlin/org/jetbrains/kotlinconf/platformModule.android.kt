package org.jetbrains.kotlinconf

import android.app.Application
import androidx.preference.PreferenceManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

fun platformModule(
    application: Application,
    notificationIconId: Int,
    notificationConfig: NotificationPlatformConfiguration.Android,
): Module {
    return module {
        single<ObservableSettings> {
            SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(application))
        }
        single<LocalNotificationService> {
            AndroidLocalNotificationService(
                timeProvider = get(),
                context = application,
                iconId = notificationIconId,
                logger = get(),
            )
        }
        single<NotificationPlatformConfiguration> { notificationConfig}
    }
}
