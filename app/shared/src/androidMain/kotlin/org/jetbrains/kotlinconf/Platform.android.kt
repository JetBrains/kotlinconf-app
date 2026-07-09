package org.jetbrains.kotlinconf

actual fun getPlatformId(): String = "android"

internal actual val isMacPlatform: Boolean = false