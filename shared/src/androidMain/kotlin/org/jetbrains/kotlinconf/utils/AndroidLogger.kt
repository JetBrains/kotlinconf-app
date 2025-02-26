package org.jetbrains.kotlinconf.utils

import android.util.Log

class AndroidLogger : Logger {
    override fun log(tag: String, message: String) {
        Log.w(tag, message)
    }
}
