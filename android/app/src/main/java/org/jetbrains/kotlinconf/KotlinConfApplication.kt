package org.jetbrains.kotlinconf

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.russhwolf.settings.PlatformSettings
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository
import org.jetbrains.kotlinconf.model.KotlinConfDataRepository.Error.*
import org.jetbrains.kotlinconf.presentation.DataRepository

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
            FAILED_TO_DELETE_RATING -> R.string.msg_failed_to_delete_vote
            FAILED_TO_POST_RATING -> R.string.msg_failed_to_post_vote
            FAILED_TO_GET_DATA -> R.string.msg_failed_to_get_data
            EARLY_TO_VOTE -> R.string.msg_early_vote
            LATE_TO_VOTE -> R.string.msg_late_vote
            else -> R.string.unknown_error
        }
        toast(message)
        error.printStackTrace()
    }
}