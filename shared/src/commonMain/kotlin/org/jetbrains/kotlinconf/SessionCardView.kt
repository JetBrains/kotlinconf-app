package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate
import org.jetbrains.kotlinconf.utils.dayAndMonth
import org.jetbrains.kotlinconf.utils.time

data class SessionCardView(
    val id: SessionId,
    val title: String,
    val speakerLine: String,
    val locationLine: String,
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val state: SessionState,
    val speakerIds: List<SpeakerId>,
    val vote: Score?,
    val timeLine: String = buildString {
        append(startsAt.dayAndMonth())
        append(", ")
        append(startsAt.time())
        append("-")
        append(endsAt.time())
    },
    val isFavorite: Boolean,
    val description: String,
    val tags: List<String>,
    val badgeTimeLine: String = buildString {
        append(startsAt.time())
        append("-")
        append(endsAt.time())
    },
    val isLightning: Boolean = endsAt.timestamp - startsAt.timestamp <= 15 * 60 * 1000,
    val startsInMinutes: Int?,
)

val SessionCardView.isLive get() = state == SessionState.Live
val SessionCardView.isUpcoming get() = state == SessionState.Upcoming
val SessionCardView.isPast get() = state == SessionState.Past

val Session.isLightning: Boolean get() = endsAt.timestamp - startsAt.timestamp <= 15 * 60 * 1000
