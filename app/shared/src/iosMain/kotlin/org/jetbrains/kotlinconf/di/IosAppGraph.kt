package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.observable.makeObservable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import org.jetbrains.kotlinconf.Flags
import org.jetbrains.kotlinconf.IOSLocalNotificationService
import platform.Foundation.NSUserDefaults

@DependencyGraph(AppScope::class)
interface IosAppGraph : AppGraph {

    @Provides
    @SingleIn(AppScope::class)
    @OptIn(ExperimentalSettingsApi::class)
    fun provideSettings(): ObservableSettings =
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)

    @Provides
    @SingleIn(AppScope::class)
    fun provideNotificationPlatformConfiguration(): NotificationPlatformConfiguration =
        NotificationPlatformConfiguration.Ios(
            showPushNotification = true,
            askNotificationPermissionOnStart = false,
            notificationSoundName = null,
        )

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides platformFlags: Flags,
        ): IosAppGraph
    }
}
