package org.jetbrains.kotlinconf.utils

import android.util.Log
import org.koin.core.annotation.Singleton

@Singleton
class AndroidLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        Log.w(tag, lazyMessage())
    }
}
