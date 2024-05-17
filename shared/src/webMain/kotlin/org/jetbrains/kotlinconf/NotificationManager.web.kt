package org.jetbrains.kotlinconf

private external object Notification {
    fun requestPermission(callback: (String) -> Unit)
}

private fun registerNotificationByServiceWorker(delay: Long, title: String, message: String): Unit =
    js(
        """{
        if (typeof navigator === "undefined" || !navigator.serviceWorker?.ready) return;
        navigator.serviceWorker.ready.then((registration) => {
          registration.active.postMessage({
            command: 'register-notification',
            title: title,
            body: message,
            delay: Number(delay),
          });
        });
        }"""
    )

private fun cancelNotificationByServiceWorker(title: String): Unit =
    js(
        """{
        if (typeof navigator === "undefined" || !navigator.serviceWorker?.ready) return;
        navigator.serviceWorker.ready.then((registration) => {
          registration.active.postMessage({
            command: 'cancel-notification',
            title: title,
          });
        });
        }"""
    )

actual class NotificationManager actual constructor(
    context: ApplicationContext
) {
    private var notificationAllowed = false

    actual fun requestPermission() {
        Notification.requestPermission {
            if (it == "granted") {
                notificationAllowed = true
            } else {
                notificationAllowed = false
            }
        }
    }

    actual fun schedule(delay: Long, title: String, message: String): String? {
        if (!notificationAllowed) return null

        registerNotificationByServiceWorker(delay, title, message)
        return title
    }

    actual fun cancel(title: String) {
        if (!notificationAllowed) return
        cancelNotificationByServiceWorker(title)
    }

}