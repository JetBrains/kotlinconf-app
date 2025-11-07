package org.jetbrains.kotlinconf

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class PartnerId(val id: String)

class Partner(
    val id: PartnerId,
    val name: String,
    val description: String,
    val icon: DrawableResource,
    val url: String,
)

@Serializable
enum class Theme {
    SYSTEM,
    LIGHT,
    DARK,
}

@Serializable
data class NotificationSettings(
    val sessionReminders: Boolean,
    val scheduleUpdates: Boolean,
) {
    fun hasAnyEnabled() = sessionReminders || scheduleUpdates
}
