package org.jetbrains.kotlinconf.di

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Property
import org.koin.core.annotation.Singleton

@Module
@Configuration
class BaseAndroidAppModule {

    @Singleton
    fun provideApplicationContext(application: Application): Context = application

    @Singleton
    fun provideSettings(application: Application): ObservableSettings =
        SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(application))

    @Singleton
    @FileStorageDir
    fun provideFileStorageDir(application: Application): String =
        application.filesDir.resolve("files").absolutePath

    @Singleton
    fun provideNotificationPlatformConfiguration(
        @Property(NOTIFICATION_ICON) iconRes: Int,
    ): NotificationPlatformConfiguration =
         NotificationPlatformConfiguration.Android(
            notificationIconResId = iconRes,
            showPushNotification = true,
        )

    companion object {
        const val NOTIFICATION_ICON = "iconRes"
    }
}
