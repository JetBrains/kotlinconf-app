package org.jetbrains.kotlinconf

// TODO implement
actual class NotificationManager actual constructor(
    context: ApplicationContext
) {
    actual fun requestPermission() {
    }

    actual fun schedule(delay: Long, title: String, message: String): String? {
        return null
    }

    actual fun cancel(title: String) {
    }

}
