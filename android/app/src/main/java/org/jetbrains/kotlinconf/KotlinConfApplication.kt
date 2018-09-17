package org.jetbrains.kotlinconf

import android.app.*
import android.content.*
import android.support.multidex.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.model.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.storage.*

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