package org.jetbrains.kotlinconf.android

import android.app.Application
import com.jetbrains.kotlinconf.R
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import org.jetbrains.kotlinconf.initApp
import org.jetbrains.kotlinconf.utils.AndroidLogger

class KotlinConfApplication : Application(), MetroApplication {

    val appGraph: AndroidAppGraph by lazy {
        createGraphFactory<AndroidAppGraph.Factory>().create(
            application = this,
            iconRes = R.drawable.kotlinconf_notification_icon,
        )
    }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override fun onCreate() {
        super.onCreate()

        initApp(
            appGraph = appGraph,
            platformLogger = AndroidLogger(),
        )
    }
}
