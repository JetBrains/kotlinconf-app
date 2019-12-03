package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.storage.*

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class NotificationManager(context: ApplicationContext) {
    fun requestPermission()

    fun schedule(delay: Long, title: String, message: String): String?

    fun cancel(title: String)
}
