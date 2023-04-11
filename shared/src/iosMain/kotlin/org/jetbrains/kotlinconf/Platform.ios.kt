package org.jetbrains.kotlinconf

import platform.Foundation.*

actual fun generateUserId(): String = "ios-" + NSUUID.UUID().UUIDString
