package org.jetbrains.kotlinconf.utils

import android.util.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
class AndroidLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        Log.w(tag, lazyMessage())
    }
}
