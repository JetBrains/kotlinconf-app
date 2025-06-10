package org.jetbrains.kotlinconf

import androidx.compose.runtime.compositionLocalOf
import kotlinx.serialization.Serializable

@Serializable
data class Flags(
    val enableBackOnMainScreens: Boolean = true,
    val supportsLocalNotifications: Boolean = true,
    val supportsRemoteNotifications: Boolean = true,
    val rippleEnabled: Boolean = true,
    val redirectFeedbackToSessionPage: Boolean = false,
    val hideKeyboardOnDrag: Boolean = false,
    val useFakeTime:  Boolean = false,
    val debugLogging: Boolean = false,
) {
    // For backward compatibility
    val supportsNotifications: Boolean
        get() = supportsLocalNotifications && supportsRemoteNotifications
}

val LocalFlags = compositionLocalOf<Flags> {
    error("LocalFlags must be part of the call hierarchy to provide configuration")
}
