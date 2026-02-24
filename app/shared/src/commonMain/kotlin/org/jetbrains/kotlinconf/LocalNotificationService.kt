package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDateTime

interface LocalNotificationService {
    suspend fun requestPermission(): Boolean

    fun post(
        localNotificationId: LocalNotificationId,
        title: String,
        message: String,
        time: LocalDateTime? = null,
    )

    fun cancel(localNotificationId: LocalNotificationId)
}
