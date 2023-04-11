package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.storage.ApplicationContext

actual class NotificationManager actual constructor(context: ApplicationContext) {
    actual fun requestPermission() {
    }

    actual fun schedule(
        delay: Long,
        title: String,
        message: String
    ): String? {
        TODO("Not yet implemented")
    }

    actual fun cancel(title: String) {
    }
}