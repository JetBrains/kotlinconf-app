package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDateTime

class NotImplementedNotificationService : NotificationService {
    override suspend fun requestPermission(): Boolean = false
    override fun post(notificationId: String, title: String, message: String, time: LocalDateTime?) {}
    override fun cancel(notificationId: String) {}
}
