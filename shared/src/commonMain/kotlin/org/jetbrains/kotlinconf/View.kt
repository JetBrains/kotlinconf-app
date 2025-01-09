package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.day_1
import kotlinconfapp.shared.generated.resources.day_2
import kotlinconfapp.shared.generated.resources.day_3
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.kotlinconf.utils.time

data class Agenda(
    val days: List<Day> = emptyList()
)

data class Speakers(
    val all: List<Speaker> = emptyList()
) {
    private val dictById = all.associateBy { it.id }
    operator fun get(id: SpeakerId): Speaker? = dictById[id]
}

@OptIn(ExperimentalResourceApi::class)
enum class EventDay(val title: StringResource) {
    May22(Res.string.day_1),
    May23(Res.string.day_2),
    May24(Res.string.day_3);

    companion object {
        fun from(value: Int) = when (value) {
            22 -> May22
            23 -> May23
            else -> May24
        }
    }
}

data class Day(
    val day: EventDay,
    val timeSlots: List<TimeSlot>
) {
    val title: StringResource
        get() = day.title
}

data class TimeSlot(
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val isLive: Boolean,
    val isFinished: Boolean,
    val sessions: List<SessionCardView>,
    val isBreak: Boolean,
    val isLunch: Boolean,
    val isParty: Boolean
) {
    val title: String = if (isLunch || isBreak) {
        sessions.firstOrNull()?.title ?: ""
    } else {
        "${startsAt.time()}-${endsAt.time()}"
    }

    val key: String =
        "${startsAt.timestamp}-${endsAt.timestamp}-$title-$isBreak-$isParty-$isLunch-${startsAt.dayOfMonth}"

    val duration: String = "${(endsAt.timestamp - startsAt.timestamp) / 1000 / 60} MIN"
}

fun Conference.buildAgenda(
    favorites: Set<SessionId>,
    votes: List<VoteInfo>,
    now: GMTDate
): Agenda {
    val days = sessions
        .groupBy { it.startsAt.dayOfMonth }
        .toList()
        .map { (day, sessions) ->
            Day(
                EventDay.from(day),
                sessions.groupByTime(conference = this, now, favorites, votes)
            )
        }
        .sortedBy { it.day }

    return Agenda(days)
}

fun List<Session>.groupByTime(
    conference: Conference,
    now: GMTDate,
    favorites: Set<SessionId>,
    votes: List<VoteInfo>,
): List<TimeSlot> {
    val slots = filterNot { it.isLightning }
        .map { it.startsAt to it.endsAt }
        .distinct()
        .sortedBy { it.first }

    return slots.map { (start, end) ->
        val cards: List<SessionCardView> = filter { it.startsAt >= start && it.endsAt <= end }
            .map {
                it.asSessionCard(conference, now, favorites, votes)
            }

        val isBreak = cards.all { it.isBreak }
        val isLunch = cards.all { it.isLunch }
        val isParty = cards.all { it.isParty }
        val isLive = start <= now && now < end
        val isFinished = end <= now

        TimeSlot(start, end, isLive, isFinished, cards, isBreak, isLunch, isParty)
    }
}

fun Session.asSessionCard(
    conference: Conference,
    now: GMTDate,
    favorites: Set<SessionId>,
    votes: List<VoteInfo>,
): SessionCardView {
    val isFinished = endsAt <= now
    val vote = votes.find { it.sessionId == id }?.score
    return SessionCardView(
        id = id,
        title = title,
        speakerLine = speakerLine(conference),
        locationLine = location,
        isFavorite = id in favorites,
        startsAt = startsAt,
        endsAt = endsAt,
        speakerIds = speakerIds,
        isFinished = isFinished,
        vote = vote,
        description = description,
        tags = tags ?: emptyList()
    )
}

fun Session.speakerLine(conference: Conference): String {
    val speakers = conference.speakers.filter { it.id in speakerIds }
    return speakers.joinToString { it.name }
}
