package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
class Conference(
    val sessions: List<Session> = emptyList(),
    val speakers: List<Speaker> = emptyList(),
)

@Serializable
class Speaker(
    val id: SpeakerId,
    val name: String,
    val position: String,
    val description: String,
    val photoUrl: String,
)

@Serializable
class Session(
    val id: SessionId,
    val title: String,
    val description: String,
    val speakerIds: List<SpeakerId>,
    val location: String,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val tags: List<String>? = null,
    val videoUrl: String? = null,
)

@Serializable
@JvmInline
value class SpeakerId(val id: String)

@Serializable
@JvmInline
value class SessionId(val id: String)

@Serializable
data class VoteInfo(
    val sessionId: SessionId,
    val score: Score?,
)

@Serializable
class FeedbackInfo(
    val sessionId: SessionId,
    val value: String
)

@Serializable
enum class Score(val value: Int) {
    GOOD(1),
    OK(0),
    BAD(-1);

    companion object {
        fun fromValue(value: Int): Score? = when (value) {
            1 -> GOOD
            0 -> OK
            -1 -> BAD
            else -> null
        }
    }
}

@Serializable
class Votes(
    val votes: List<VoteInfo> = emptyList()
)
