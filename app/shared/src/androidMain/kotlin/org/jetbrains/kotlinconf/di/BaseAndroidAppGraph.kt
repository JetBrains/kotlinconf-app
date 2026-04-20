//package org.jetbrains.kotlinconf.di
//
//import android.app.Application
//import android.content.Context
//import androidx.preference.PreferenceManager
//import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
//import com.russhwolf.settings.ObservableSettings
//import com.russhwolf.settings.SharedPreferencesSettings
//import dev.zacsweers.metro.AppScope
//import dev.zacsweers.metro.Provides
//import dev.zacsweers.metro.Qualifier
//import dev.zacsweers.metro.SingleIn
//import dev.zacsweers.metrox.android.MetroAppComponentProviders
//
//interface BaseAndroidAppGraph : AppGraph, MetroAppComponentProviders {
//
//    @Provides
//    @SingleIn(AppScope::class)
//    fun provideApplicationContext(application: Application): Context = application
//
//    @Provides
//    @SingleIn(AppScope::class)
//    fun provideSettings(application: Application): ObservableSettings =
//        SharedPreferencesSettings(PreferenceManager.getDefaultSharedPreferences(application))
//
//    @Provides
//    @SingleIn(AppScope::class)
//    @FileStorageDir
//    fun provideFileStorageDir(application: Application): String =
//        application.filesDir.resolve("files").absolutePath
//
//    @Provides
//    @SingleIn(AppScope::class)
//    fun provideNotificationPlatformConfiguration(
//        @NotificationIcon iconRes: Int,
//    ): NotificationPlatformConfiguration =
//         NotificationPlatformConfiguration.Android(
//            notificationIconResId = iconRes,
//            showPushNotification = true,
//        )
//}
//
//@Qualifier
//annotation class NotificationIcon
