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

private data class SlotTimes(
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
)

fun List<Session>.groupByTime(
    conference: Conference,
    now: LocalDateTime,
    favorites: Set<SessionId>,
    votes: Map<SessionId, VoteInfo>,
): List<TimeSlot> {
    val slots: List<SlotTimes> =
        filterNot { it.isLightning }
            .sortedBy { it.startsAt }
            .map { SlotTimes(it.startsAt, it.endsAt) }

    val slotsToSessions: Map<SlotTimes, MutableList<SessionCardView>> =
        slots.associateWith { mutableListOf() }

    this.forEach { session ->
        val slot = slots.find { (start, end) -> session.startsAt >= start && session.endsAt <= end } ?: return@forEach
        slotsToSessions.getValue(slot).add(
            session.asSessionCard(conference, now, favorites, votes[session.id])
        )
    }

    return slotsToSessions.mapNotNull { (slot, sessions) ->
        if (sessions.isNotEmpty()) {
            TimeSlot(
                startsAt = slot.startsAt,
                endsAt = slot.endsAt,
                state = SessionState.from(slot.startsAt, slot.endsAt, now),
                sessions = sessions.sortedBy { it.isLightning },
            )
        } else {
            null
        }
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
