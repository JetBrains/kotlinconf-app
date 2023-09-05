package org.jetbrains.kotlinconf

import android.app.Application

actual class ApplicationContext(
    val application: Application,
    val notificationIcon: Int
)
