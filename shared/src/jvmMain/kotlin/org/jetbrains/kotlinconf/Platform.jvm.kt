package org.jetbrains.kotlinconf

import java.util.*

actual fun generateUserId(): String = "desktop-" + UUID.randomUUID().toString()