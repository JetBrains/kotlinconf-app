package org.jetbrains.kotlinconf.wrapped

import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

/**
 * Lightweight, on-device usage counters for the current conference year.
 *
 * These never leave the device; they only feed the Wrapped summary.
 */
@Serializable
data class UsageStats(
    val appLaunches: Int = 0,
    val sessionViews: Map<SessionId, Int> = emptyMap(),
    val speakerViews: Map<SpeakerId, Int> = emptyMap(),
) {
    val totalSessionViews: Int get() = sessionViews.values.sum()
    val totalSpeakerViews: Int get() = speakerViews.values.sum()

    fun withAppLaunch(): UsageStats = copy(appLaunches = appLaunches + 1)

    fun withSessionView(sessionId: SessionId): UsageStats =
        copy(sessionViews = sessionViews.increment(sessionId))

    fun withSpeakerView(speakerId: SpeakerId): UsageStats =
        copy(speakerViews = speakerViews.increment(speakerId))

    private fun <K> Map<K, Int>.increment(key: K): Map<K, Int> =
        this + (key to (this[key] ?: 0) + 1)
}
