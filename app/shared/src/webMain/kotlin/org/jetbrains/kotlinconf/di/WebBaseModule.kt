package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@Configuration
class WebBaseModule {
    @Singleton
    @OptIn(ExperimentalSettingsApi::class)
    fun provideSettings(): ObservableSettings = StorageSettings().makeObservable()

    @Singleton
    fun provideNotificationPlatformConfiguration(): NotificationPlatformConfiguration =
        NotificationPlatformConfiguration.Web(false, null)
}
