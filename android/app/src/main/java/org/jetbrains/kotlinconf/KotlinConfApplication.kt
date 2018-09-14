package org.jetbrains.kotlinconf

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.russhwolf.settings.PlatformSettings
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository.Error.*
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository.Unauthorized
import org.jetbrains.kotlinconf.presentation.DataRepository
import java.net.ConnectException

class KotlinConfApplication : Application(), AnkoLogger {

    val dataRepository: DataRepository by lazy {
        val settingsFactory = PlatformSettings.Factory(applicationContext)
        KotlinConfDataRepository(settingsFactory, ::showError)
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

    private fun showError(error: Throwable) {
        val message = when (error) {
            is Unauthorized -> R.string.unauthorized_error
            is ConnectException -> return // It means that user is offline
            else -> R.string.unknown_error
        }
        toast(message)
        error.printStackTrace()
    }
}