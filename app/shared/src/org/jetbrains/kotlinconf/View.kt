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
    val talkSlots: List<SlotTimes> =
        filterNot { it.isLightning || (it.speakerIds.isEmpty() && it.tags.isNullOrEmpty()) }
            .map { SlotTimes(it.startsAt, it.endsAt) }
            .distinct()

    val slotsToSessions = mutableMapOf<SlotTimes, MutableList<SessionCardView>>()

    val speakersById = conference.speakers.associateBy { it.id }

    this.forEach { session ->
        val exactSlot = SlotTimes(session.startsAt, session.endsAt)
        val isServiceEvent = session.speakerIds.isEmpty() && session.tags.isNullOrEmpty()
        // Only lightning talks share a containing talk's slot. Breaks and regular
        // sessions retain their own times, even inside a full-day workshop.
        val slot = if (session.isLightning && !isServiceEvent) {
            talkSlots.filter { (start, end) -> session.startsAt >= start && session.endsAt <= end }
                .minByOrNull { it.endsAt - it.startsAt } ?: exactSlot
        } else exactSlot
        slotsToSessions.getOrPut(slot) { mutableListOf() }.add(
            session.asSessionCard(speakersById, now, favorites, votes[session.id])
        )
    }

    return slotsToSessions.entries
        .sortedWith(compareBy({ it.key.startsAt }, { it.key.endsAt }))
        .map { (slot, sessions) ->
            TimeSlot(
                startsAt = slot.startsAt,
                endsAt = slot.endsAt,
                state = SessionState.from(slot.startsAt, slot.endsAt, now),
                sessions = sessions.sortedBy { it.isLightning },
            )
        }
}

fun Session.asSessionCard(
    speakersById: Map<SpeakerId, Speaker>,
    now: LocalDateTime,
    favorites: Set<SessionId>,
    vote: VoteInfo?,
): SessionCardView {
    return SessionCardView(
        id = id,
        title = title,
        speakerLine = speakerIds.mapNotNull { speakersById[it] }.joinToString { it.name },
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
