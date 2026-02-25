package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import org.jetbrains.kotlinconf.Flags
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

@DependencyGraph(AppScope::class)
interface IosAppGraph : AppGraph {

    @Provides
    @SingleIn(AppScope::class)
    @OptIn(ExperimentalSettingsApi::class)
    fun provideSettings(): ObservableSettings =
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)

    @Provides
    @SingleIn(AppScope::class)
    @FileStorageDir
    fun provideFileStorageDir(): String {
        val storageDir = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory, NSUserDomainMask, true
        ).first() as String
        return "$storageDir/files"
    }

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
