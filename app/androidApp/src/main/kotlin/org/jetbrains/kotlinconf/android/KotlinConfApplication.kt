package org.jetbrains.kotlinconf.android

import android.app.Application
import com.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.di.BaseAndroidAppModule.Companion.NOTIFICATION_ICON
import org.jetbrains.kotlinconf.initApp
import org.jetbrains.kotlinconf.utils.AndroidLogger
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.PropertyValue

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

//TODO use/read PropertyValue
@PropertyValue(NOTIFICATION_ICON)
val iconRes = R.drawable.kotlinconf_notification_icon