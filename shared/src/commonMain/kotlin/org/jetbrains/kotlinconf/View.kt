package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.kotlinconf.utils.time
import kotlin.math.roundToInt

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
enum class EventDay() {
    May22(),
    May23(),
    May24();

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
    // TODO Review usages of this later
    val title: String
        get() = day.toString()
}

data class TimeSlot(
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val isLive: Boolean,
    val isFinished: Boolean,
    val isUpcoming: Boolean,
    val sessions: List<SessionCardView>,
    val title: String = "${startsAt.time()}-${endsAt.time()}",
)

fun Conference.buildAgenda(
    favorites: Set<SessionId>,
    votes: List<VoteInfo>,
    now: GMTDate
): Agenda {
    val days = sessions
        .groupBy { it.startsAt.dayOfMonth }
        .map { (day, sessions) ->
            Day(
                day = EventDay.from(day),
                timeSlots = sessions.groupByTime(
                    conference = this,
                    now = now,
                    favorites = favorites,
                    votes = votes,
                )
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

        val isLive = start <= now && now < end
        val isFinished = end <= now
        val isUpcoming = start > now

        TimeSlot(
            startsAt = start,
            endsAt = end,
            isLive = isLive,
            isFinished = isFinished,
            isUpcoming = isUpcoming,
            sessions = cards,
        )
    }
}

fun Session.asSessionCard(
    conference: Conference,
    now: GMTDate,
    favorites: Set<SessionId>,
    votes: List<VoteInfo>,
): SessionCardView {
    return SessionCardView(
        id = id,
        title = title,
        speakerLine = speakerLine(conference),
        locationLine = location,
        isFavorite = id in favorites,
        startsAt = startsAt,
        endsAt = endsAt,
        isLive = startsAt <= now && now < endsAt,
        speakerIds = speakerIds,
        isFinished = endsAt <= now,
        isUpcoming = startsAt > now,
        vote = votes.find { it.sessionId == id }?.score,
        description = description,
        tags = tags ?: emptyList(),
        startsInMinutes = (startsAt.timestamp - now.timestamp).let { diff ->
            // In the next 30 minutes
            if (diff > 0 && diff <= 30 * 60 * 1000) {
                (diff / 60.0 / 1000.0).roundToInt()
            } else {
                null
            }
        }
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

data class ServiceEvent(
    val id: String,
    val title: String,
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val isLive: Boolean,
    val startsInMinutes: Int?,
)
