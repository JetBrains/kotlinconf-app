package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDateTime

class EmptyLocalNotificationService : LocalNotificationService {
    override suspend fun requestPermission(): Boolean = false
    override fun post(localNotificationId: LocalNotificationId, title: String, message: String, time: LocalDateTime?) {}
    override fun cancel(localNotificationId: LocalNotificationId) {}
}
