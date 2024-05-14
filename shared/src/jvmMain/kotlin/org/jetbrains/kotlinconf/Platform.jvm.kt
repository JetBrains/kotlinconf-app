package org.jetbrains.kotlinconf

import java.util.UUID

actual fun generateUserId(): String = "desktop-" + UUID.randomUUID().toString()