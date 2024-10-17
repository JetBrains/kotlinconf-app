package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import org.jetbrains.kotlinconf.utils.*

data class SessionCardView(
    val id: String,
    val title: String,
    val speakerLine: String,
    val locationLine: String,
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val speakerIds: List<String>,
    val vote: Score?,
    val timeLine: String = buildString {
        append(startsAt.dayAndMonth())
        append(", ")
        append(startsAt.time())
        append("-")
        append(endsAt.time())
    },
    val isFavorite: Boolean,
    val isFinished: Boolean,
    val description: String,
    val tags: List<String>,
    val badgeTimeLine: String = buildString {
        append(startsAt.time())
        append("-")
        append(endsAt.time())
    }
) {
    val isBreak: Boolean = title == "Break" || title == "Breakfast" || title == "Coffee Break"

    val isLunch: Boolean = title == "Lunch"

    val isParty: Boolean = title.contains("Party")

    val isLightning: Boolean = endsAt.timestamp - startsAt.timestamp <= 15 * 60 * 1000

    val key: String =
        "${startsAt.timestamp}-${endsAt.timestamp}-$title-$isBreak-$isParty-$isLunch-${startsAt.dayOfMonth}"
}

val Session.isLightning: Boolean get() = endsAt.timestamp - startsAt.timestamp <= 15 * 60 * 1000