package org.jetbrains.kotlinconf

import android.app.*
import android.content.*
import android.support.multidex.*
import org.jetbrains.anko.*
import org.jetbrains.kotlinconf.api.END_POINT
import org.jetbrains.kotlinconf.model.*
import org.jetbrains.kotlinconf.presentation.*
import org.jetbrains.kotlinconf.storage.*
import java.util.*

class KotlinConfApplication : Application(), AnkoLogger {

    val dataRepository: DataRepository by lazy {
        val settingsFactory = PlatformSettings(applicationContext)
        KotlinConfDataRepository(END_POINT, getUserId(), settingsFactory)
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

    private fun getUserId(): String = "android-" + UUID.randomUUID().toString()
}