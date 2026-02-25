package org.jetbrains.kotlinconf

import kotlinx.serialization.Serializable

@Serializable
enum class Theme {
    SYSTEM,
    LIGHT,
    DARK,

    ;
    companion object {
        operator fun invoke(text: String?): Theme = valueOf(text ?: return SYSTEM)
    }
}

@Serializable
data class NotificationSettings(
    val sessionReminders: Boolean,
    val scheduleUpdates: Boolean,
) {
    fun hasAnyEnabled() = sessionReminders || scheduleUpdates
}
