package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDateTime

interface NotificationService {
    suspend fun requestPermission(): Boolean

    fun post(
        notificationId: String,
        title: String,
        message: String,
        time: LocalDateTime? = null,
    )

    fun cancel(notificationId: String)
}
