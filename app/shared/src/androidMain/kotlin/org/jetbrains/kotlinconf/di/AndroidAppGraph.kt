package org.jetbrains.kotlinconf.di

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import org.jetbrains.kotlinconf.Flags

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph, MetroAppComponentProviders {

    @Provides
    @SingleIn(AppScope::class)
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @SingleIn(AppScope::class)
    fun provideSettings(application: Application): ObservableSettings =
        SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(application))

    @Provides
    @SingleIn(AppScope::class)
    fun provideNotificationPlatformConfiguration(
        @NotificationIcon iconRes: Int,
    ): NotificationPlatformConfiguration =
         NotificationPlatformConfiguration.Android(
            notificationIconResId = iconRes,
            showPushNotification = true,
        )

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides application: Application,
            @Provides @NotificationIcon iconRes: Int,
            @Provides platformFlags: Flags = Flags(),
        ): AndroidAppGraph
    }
}

@Qualifier
annotation class NotificationIcon
