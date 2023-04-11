package org.jetbrains.kotlinconf

import java.util.*

actual fun generateUserId(): String = "android-" + UUID.randomUUID().toString()
