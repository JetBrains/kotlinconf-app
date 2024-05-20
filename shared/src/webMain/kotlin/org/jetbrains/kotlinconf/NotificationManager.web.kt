package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.utils.GRANTED_PERMISSION
import org.jetbrains.kotlinconf.utils.Notification

actual class NotificationManager actual constructor(
    context: ApplicationContext
) {
    private var notificationAllowed =
        Notification.permission == GRANTED_PERMISSION

    actual fun requestPermission() {
        Notification.requestPermission {
            if (it == GRANTED_PERMISSION) {
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

private fun registerNotificationByServiceWorker(delay: Long, title: String, message: String): Unit =
    js(
        """{
        if (typeof navigator === "undefined" || navigator.serviceWorker == null || !navigator.serviceWorker.ready) return;
        navigator.serviceWorker.ready.then(function (registration) {
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
        if (typeof navigator === "undefined" || navigator.serviceWorker == null || !navigator.serviceWorker.ready) return;
        navigator.serviceWorker.ready.then(function (registration) {
          registration.active.postMessage({
            command: 'cancel-notification',
            title: title,
          });
        });
        }"""
    )

