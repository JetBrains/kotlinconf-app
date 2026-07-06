// ABOUTME: Wire-format models for the admin JSON endpoints, mirroring the backend's AdminDtos.
// ABOUTME: Kept in the admin module so the SPA stays self-contained; reuses core's SessionId/Score.
package org.jetbrains.kotlinconf.admin

import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionId

@Serializable
data class AdminVoteRow(
    val userId: String,
    val sessionId: SessionId,
    val score: Score?,
)

@Serializable
data class AdminFeedbackRow(
    val userId: String,
    val sessionId: SessionId,
    val value: String,
)

/** Emoji shown for a score, matching the original votes.html dashboard. */
fun Score.emoji(): String = when (this) {
    Score.GOOD -> "👍" // 👍
    Score.OK -> "😐" // 😐
    Score.BAD -> "👎" // 👎
}
