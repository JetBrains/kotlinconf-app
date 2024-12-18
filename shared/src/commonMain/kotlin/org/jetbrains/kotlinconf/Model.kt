package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.serialization.*
import org.jetbrains.kotlinconf.utils.*
import kotlin.jvm.JvmInline

typealias GMTDateSerializable = @Serializable(GMTDateSerializer::class) GMTDate

@Serializable
@JvmInline
value class SpeakerId(val id: String) {
    override fun toString(): String = id
}

@Serializable
@JvmInline
value class SessionId(val id: String) {
    override fun toString(): String = id
}

@Serializable
class Conference(
    val sessions: List<Session> = emptyList(),
    val speakers: List<Speaker> = emptyList(),
)

@Serializable
class Votes(
    val votes: List<VoteInfo> = emptyList()
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
    val startsAt: GMTDateSerializable,
    val endsAt: GMTDateSerializable,
    val tags: List<String>? = null
) {
    val timeLine get() = startsAt.time() + " - " + endsAt.time()
}

@Serializable
class VoteInfo(
    val sessionId: SessionId,
    val score: Score?
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