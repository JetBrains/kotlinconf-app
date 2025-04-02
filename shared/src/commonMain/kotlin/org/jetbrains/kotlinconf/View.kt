package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.utils.DateTimeFormatting

data class Day(
    val date: LocalDate,
    val timeSlots: List<TimeSlot>
)

data class TimeSlot(
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val state: SessionState,
    val sessions: List<SessionCardView>,
) {
    val title: String = DateTimeFormatting.timeToTime(startsAt, endsAt)
}

val TimeSlot.isLive get() = state == SessionState.Live

fun Conference.buildAgenda(
    favorites: Set<SessionId>,
    votes: List<VoteInfo>,
    now: LocalDateTime,
): List<Day> {
    val votesBySessionId = votes.associateBy { it.sessionId }
    return sessions
        .groupBy { it.startsAt.date }
        .map { (date, sessions) ->
            Day(
                date = date,
                timeSlots = sessions.groupByTime(
                    conference = this,
                    now = now,
                    favorites = favorites,
                    votes = votesBySessionId,
                )
            )
        }
        .sortedBy { it.date }
}

fun List<Session>.groupByTime(
    conference: Conference,
    now: LocalDateTime,
    favorites: Set<SessionId>,
    votes: Map<SessionId, VoteInfo>,
): List<TimeSlot> {
    val slots = filterNot { it.isLightning }
        .map { it.startsAt to it.endsAt }
        .distinct()
        .sortedBy { it.first }

    val handledSessionIds = mutableSetOf<SessionId>()
    return slots.map { (start, end) ->
        val cards: List<SessionCardView> =
            filter { it.startsAt >= start && it.endsAt <= end && it.id !in handledSessionIds }
                .map {
                    it.asSessionCard(conference, now, favorites, votes[it.id])
                }

        handledSessionIds += cards.map { it.id }

        TimeSlot(
            startsAt = start,
            endsAt = end,
            state = SessionState.from(start, end, now),
            sessions = cards,
        )
    }
}

fun Session.asSessionCard(
    conference: Conference,
    now: LocalDateTime,
    favorites: Set<SessionId>,
    vote: VoteInfo?,
): SessionCardView {
    return SessionCardView(
        id = id,
        title = title,
        speakerLine = speakerLine(conference),
        locationLine = location,
        isFavorite = id in favorites,
        startsAt = startsAt,
        endsAt = endsAt,
        state = SessionState.from(startsAt, endsAt, now),
        speakerIds = speakerIds,
        vote = vote?.score,
        description = description,
        tags = tags?.toSet() ?: emptySet(),
        startsInMinutes = (startsAt - now).inWholeMinutes.toInt().let { diff ->
            if (diff in 1..30) diff else null
        },
        videoUrl = videoUrl,
    )
}

fun Session.speakerLine(conference: Conference): String {
    val speakers = conference.speakers.filter { it.id in speakerIds }
    return speakers.joinToString { it.name }
}

data class NewsDisplayItem(
    val id: String,
    val photoUrl: String?,
    val date: String,
    val title: String,
    val content: String,
)
