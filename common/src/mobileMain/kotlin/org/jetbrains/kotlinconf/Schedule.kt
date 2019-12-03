package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.presentation.*

internal fun List<SessionData>.groupByDay(service: ConferenceService): List<SessionGroup> = groupBy { it.startsAt }
    .map { (startsAt, sessions) ->
        val month = startsAt.month
        val day = startsAt.dayOfMonth
        val endsAt = sessions.first().endsAt
        val time = "${startsAt.time()}–${endsAt.time()}"

        val first = sessions.firstOrNull {
            it.isServiceSession && it.isPlenumSession
        }

        if (first != null) {
            val title = buildString {
                append(time)
                append('\n')
                val name = first.title.toUpperCase()
                append(name.takeWhile { it != '-' })
            }

            return@map SessionGroup(
                month, day, title, startsAt, emptyList(),
                lunchSection = true
            )
        }

        val cards = sessions.map { service.sessionCard(it.id) }
        SessionGroup(month, day, time, startsAt, cards)
    }.sortedBy { it.startsAt.timestamp }

internal fun List<SessionGroup>.addDayStart(): List<SessionGroup> {
    if (isEmpty()) {
        return this
    }

    val result = mutableListOf<SessionGroup>()
    var lastDay: Int? = null
    for (group in this) {
        if (group.day != lastDay) {
            result += group.makeDayHeader()
            lastDay = group.day
        }

        result += group
    }

    return result
}

internal fun SessionGroup.makeDayHeader(): SessionGroup {
    val title = "${month.displayName().toUpperCase()} $day ".repeat(10)
    return SessionGroup(
        month, day, title, startsAt, emptyList(), daySection = true
    )
}

