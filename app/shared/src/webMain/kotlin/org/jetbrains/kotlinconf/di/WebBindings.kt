package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
object WebBindings {

    @Provides
    @SingleIn(AppScope::class)
    @OptIn(ExperimentalSettingsApi::class)
    fun provideSettings(): ObservableSettings = StorageSettings().makeObservable()

    @Provides
    @SingleIn(AppScope::class)
    fun provideNotificationPlatformConfiguration(): NotificationPlatformConfiguration =
        NotificationPlatformConfiguration.Web(false, null)
}
