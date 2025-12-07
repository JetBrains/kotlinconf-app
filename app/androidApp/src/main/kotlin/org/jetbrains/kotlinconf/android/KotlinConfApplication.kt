package org.jetbrains.kotlinconf.android

import android.app.Application
import com.jetbrains.kotlinconf.R
import dev.zacsweers.metro.createGraphFactory
import org.jetbrains.kotlinconf.di.AndroidAppGraph
import org.jetbrains.kotlinconf.initApp
import org.jetbrains.kotlinconf.utils.AndroidLogger

class KotlinConfApplication : Application() {
    val appGraph: AndroidAppGraph by lazy {
        createGraphFactory<AndroidAppGraph.Factory>().create(
            application = this,
            notificationIconId = R.drawable.kotlinconf_notification_icon,
        )
    }

    override fun onCreate() {
        super.onCreate()

        initApp(
            appGraph = appGraph,
            platformLogger = AndroidLogger(),
        )
    }
}
