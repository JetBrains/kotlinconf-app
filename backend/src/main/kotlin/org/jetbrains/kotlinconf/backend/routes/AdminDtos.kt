package org.jetbrains.kotlinconf.backend.routes

import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionId

/**
 * Admin-only response DTOs. These carry the user's identifier so admin tooling
 * can join a vote with the feedback the same user left for the same session.
 * Not part of the shared client model on purpose: clients never see userId.
 */
@Serializable
data class AdminVoteInfo(
    val userId: String,
    val sessionId: SessionId,
    val score: Score?,
)

@Serializable
data class AdminFeedbackInfo(
    val userId: String,
    val sessionId: SessionId,
    val value: String,
)
