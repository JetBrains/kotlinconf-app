package org.jetbrains.kotlinconf

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository
import org.jetbrains.kotlinconf.presentation.DataRepository
import org.jetbrains.kotlinconf.storage.PlatformSettings
import java.net.ConnectException

class KotlinConfApplication : Application(), AnkoLogger {

    val dataRepository: DataRepository by lazy {
        val settingsFactory = PlatformSettings(applicationContext)
        KotlinConfDataRepository(settingsFactory)
    }

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            println(throwable)
            throwable.printStackTrace()
            throwable?.cause?.printStackTrace()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}