package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import org.jetbrains.kotlinconf.EmptyLocalNotificationService
import org.jetbrains.kotlinconf.Flags
import org.jetbrains.kotlinconf.LocalNotificationService
import org.jetbrains.kotlinconf.storage.createSettings

@DependencyGraph(AppScope::class)
interface JvmAppGraph : AppGraph {

    @Provides
    @SingleIn(AppScope::class)
    fun provideSettings(): ObservableSettings = createSettings()

    @Provides
    @SingleIn(AppScope::class)
    fun provideLocalNotificationService(): LocalNotificationService = EmptyLocalNotificationService()

    @Provides
    @SingleIn(AppScope::class)
    fun provideNotificationPlatformConfiguration(): NotificationPlatformConfiguration =
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = false,
            notificationIconPath = null,
        )

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides platformFlags: Flags = Flags(),
        ): JvmAppGraph
    }
}
