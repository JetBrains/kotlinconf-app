package org.jetbrains.kotlinconf.android

import android.app.Application
import com.jetbrains.kotlinconf.R
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import org.jetbrains.kotlinconf.initApp
import org.jetbrains.kotlinconf.platformModule
import org.jetbrains.kotlinconf.utils.AndroidLogger

class KotlinConfApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initApp(
            platformLogger = AndroidLogger(),
            platformModule = platformModule(
                application = this,
                notificationIconId = R.drawable.kotlinconf_notification_icon,
                notificationConfig = NotificationPlatformConfiguration.Android(
                    notificationIconResId = R.drawable.kotlinconf_notification_icon,
                    showPushNotification = true,
                ),
            ),
        )
    }
}
