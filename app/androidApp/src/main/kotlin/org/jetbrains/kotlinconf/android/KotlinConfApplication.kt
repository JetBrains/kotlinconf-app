package org.jetbrains.kotlinconf.android

import android.app.Application
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.di.AndroidProperties.NOTIFICATION_ICON
import org.jetbrains.kotlinconf.initApp
import org.jetbrains.kotlinconf.utils.AndroidLogger
import org.koin.android.ext.koin.androidContext

class KotlinConfApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initApp(
            platformLogger = AndroidLogger()
        ) {
            androidContext(this@KotlinConfApplication)
            properties(mapOf(
                NOTIFICATION_ICON to R.drawable.kotlinconf_notification_icon
            ))
        }
    }
}