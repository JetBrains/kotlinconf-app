package org.jetbrains.kotlinconf.android

import android.app.Application
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.initKoin
import org.jetbrains.kotlinconf.platformModule

class KotlinConfApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val platformModule = platformModule(
            application = this,
            notificationIconId = R.drawable.kotlinconf_notification_icon,
        )
        initKoin(platformModule)
    }
}
