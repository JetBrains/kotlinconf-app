package org.jetbrains.kotlinconf.flags

import androidx.compose.runtime.compositionLocalOf
import kotlinx.serialization.Serializable

@Serializable
data class Flags(
    val enableBackOnTopLevelScreens: Boolean = true,
    val supportsNotifications: Boolean = true,
    val rippleEnabled: Boolean = true,
    val hideKeyboardOnDrag: Boolean = false,
    val useFakeTime:  Boolean = false,
    val useFakeGoldenKodeeData: Boolean = false,
    val debugLogging: Boolean = false,
)

val LocalFlags = compositionLocalOf<Flags> {
    error("LocalFlags must be part of the call hierarchy to provide configuration")
}
