package org.jetbrains.kotlinconf

import androidx.compose.runtime.compositionLocalOf

data class Flags(
    val enableBackOnMainScreens: Boolean = true,
    val supportsNotifications: Boolean = true,
)

val LocalFlags = compositionLocalOf<Flags> {
    error("LocalFlags must be part of the call hierarchy to provide configuration")
}
