package org.jetbrains.kotlinconf.storage

import android.app.*

actual class ApplicationContext(
    val activity: Activity,
    val notificationIcon: Int
)