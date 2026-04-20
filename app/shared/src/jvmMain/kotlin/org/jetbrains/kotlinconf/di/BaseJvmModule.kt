package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ObservableSettings
import org.jetbrains.kotlinconf.EmptyLocalNotificationService
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.LocalNotificationService
import org.jetbrains.kotlinconf.storage.createSettings
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@Configuration
class BaseJvmModule {

    @Singleton
    fun provideSettings(): ObservableSettings = createSettings()

    @Singleton
    @FileStorageDir
    fun provideFileStorageDir(): String = "files"

    @Singleton
    fun provideLocalNotificationService(): LocalNotificationService = EmptyLocalNotificationService()

    @Singleton
    fun provideNotificationPlatformConfiguration(): NotificationPlatformConfiguration =
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = false,
            notificationIconPath = null,
        )
}
