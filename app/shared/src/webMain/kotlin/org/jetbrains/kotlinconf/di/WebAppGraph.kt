package org.jetbrains.kotlinconf.di

import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import org.jetbrains.kotlinconf.Flags

@DependencyGraph(AppScope::class)
interface WebAppGraph : AppGraph {
    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides platformFlags: Flags): WebAppGraph
    }

    @Provides
    @SingleIn(AppScope::class)
    @OptIn(ExperimentalSettingsApi::class)
    fun provideSettings(): ObservableSettings = StorageSettings().makeObservable()

    @Provides
    @SingleIn(AppScope::class)
    fun provideNotificationPlatformConfiguration(): NotificationPlatformConfiguration =
        NotificationPlatformConfiguration.Web(false, null)
}
