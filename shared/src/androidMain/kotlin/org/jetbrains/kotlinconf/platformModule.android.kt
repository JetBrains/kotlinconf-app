package org.jetbrains.kotlinconf

import android.app.Application
import androidx.preference.PreferenceManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.jetbrains.kotlinconf.utils.AndroidLogger
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

fun platformModule(
    application: Application,
    notificationIconId: Int,
): Module {
    return module {
        single<ObservableSettings> {
            SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(application))
        }
        single<NotificationService> {
            AndroidNotificationService(
                timeProvider = get(),
                context = application,
                iconId = notificationIconId,
                logger = get(),
            )
        }
        single<Logger> { AndroidLogger() }
    }
}
