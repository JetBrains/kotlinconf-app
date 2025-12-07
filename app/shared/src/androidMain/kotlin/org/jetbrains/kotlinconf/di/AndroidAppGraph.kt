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
import dev.zacsweers.metro.SingleIn
import org.jetbrains.kotlinconf.Flags

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph {

    @Provides
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @SingleIn(AppScope::class)
    fun provideSettings(application: Application): ObservableSettings =
        SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(application))

    @Provides
    @SingleIn(AppScope::class)
    fun provideNotificationPlatformConfiguration(
        notificationIconId: Int
    ): NotificationPlatformConfiguration =
         NotificationPlatformConfiguration.Android(
            notificationIconResId = notificationIconId,
            showPushNotification = true,
        )

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides application: Application,
            @Provides notificationIconId: Int, // TODO give this a qualifier
            @Provides platformFlags: Flags = Flags(),
        ): AndroidAppGraph
    }
}
