package org.jetbrains.kotlinconf.storage

import android.app.*

actual class ApplicationContext(
    val application: Application,
    val notificationIcon: Int
)
