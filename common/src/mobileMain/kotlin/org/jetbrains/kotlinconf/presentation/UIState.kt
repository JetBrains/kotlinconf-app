package org.jetbrains.kotlinconf.presentation

import io.ktor.util.date.*
import kotlinx.coroutines.flow.*
import org.jetbrains.kotlinconf.*
import kotlin.math.*

/**
 * Session displayed in single schedule group.
 */
class SessionGroup(
    val month: Month,
    val day: Int,
    val title: String,
    val startsAt: GMTDate,
    val sessions: List<SessionCard>,
    val daySection: Boolean = false,
    val lunchSection: Boolean = false
)

/**
 * All data to display session cards.
 */
class SessionCard(
    val session: SessionData,
    val date: String,
    val time: String,
    val location: RoomData,
    val isLive: CFlow<String?>,
    val speakers: List<SpeakerData>,
    val isFavorite: CFlow<Boolean>,
    val ratingData: CFlow<RatingData?>
)

/**
 * Session card time label text.
 */
fun SessionCard.displayTime(): String = buildString {
    append(date)
    append(" ")
    append(time)
}

/**
 * Check if day is workshop day.
 */
fun SessionData.isWorkshop(): Boolean =
    startsAt.dayOfMonth == 4

/**
 * Room name to display.
 */
fun RoomData.displayName(isWorkshop: Boolean): String {
    if ('-' !in name) {
        return name
    }

    val names = name.split('-')
    return when {
        isWorkshop -> names[0]
        else -> names[1]
    }.trim()
}

fun FeedPost.displayDate(): String {
    // Fri Oct 04 18:09:16 +0000 2019
    val chunks = created_at.split(" ")

    if (chunks.size < 4) {
        return created_at.take(10)
    }

    val time = chunks[3]
    val day = chunks[2]
    val month = chunks[1]
    return "$day $month $time"
}