package org.jetbrains.kotlinconf.backend

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.Session
import org.jetbrains.kotlinconf.Speaker
import java.util.concurrent.TimeUnit

private val conference = MutableSharedFlow<Conference>()
val comeBackLater = HttpStatusCode(477, "Come Back Later")
val GMT_TIME_OFFSET = 2 * 60 * 60 * 1000

@OptIn(DelicateCoroutinesApi::class)
fun Application.launchSyncJob(
    sessionizeUrl: String,
    sessionizeInterval: Long
) {
    log.info("Synchronizing each $sessionizeInterval minutes with $sessionizeUrl")
    GlobalScope.launch {
        while (true) {
            log.trace("Synchronizing to Sessionize…")
            synchronizeWithSessionize(sessionizeUrl)
            log.trace("Finished loading data from Sessionize.")
            delay(TimeUnit.MINUTES.toMillis(sessionizeInterval))
        }
    }
}

suspend fun synchronizeWithSessionize(
    sessionizeUrl: String,
) {
    val updatedValue = client.get(sessionizeUrl)
        .body<SessionizeData>()
        .toConference()

    conference.emit(updatedValue)
}

suspend fun fetchSessionizeImage(
    imagesUrl: String,
    imageId: String
): ByteArray {
    return client.get("$imagesUrl/$imageId").body<ByteArray>()
}

suspend fun getSessionizeData(): Conference = conference.first()

fun SessionizeData.toConference(): Conference {
    val tags: Map<Int, CategoryItemData> = categories
        .flatMap { it.items }
        .associateBy { it.id }

    fun findRoom(id: Int) = rooms.find { it.id == id }?.name ?: "unknown"

    val sessions = sessions.mapNotNull { it ->
        val startsAt = it.startsAt ?: return@mapNotNull null
        val endsAt = it.endsAt ?: return@mapNotNull null
        val tags = it.categoryItems.mapNotNull { tags[it]?.name }
        Session(
            it.id,
            it.displayTitle,
            it.descriptionText ?: "",
            it.speakers,
            it.roomId?.let { findRoom(it) } ?: "unknown",
            startsAt,
            endsAt,
            tags
        )
    }.mergeWorkshops()

    val speakers = speakers.map {
        Speaker(
            it.id,
            it.fullName,
            it.tagLine ?: "",
            it.bio ?: "",
            it.profilePicture ?: ""
        )
    }

    return Conference(sessions, speakers)
}

internal fun List<Session>.mergeWorkshops(): List<Session> {
    val (workshopParts, nonWorkshop) = partition { it.tags?.contains("Workshop") == true }

    // Group by room
    val workshopPartsByLocations = workshopParts.groupBy { it.location }
    val workshops = workshopPartsByLocations.values.mapNotNull { parts ->
        val startTime = parts.minOf { it.startsAt }
        val endTime = parts.maxOf { it.endsAt }
        val first = parts.find { it.title.contains("Part 1") } ?: return@mapNotNull null
        val title = first.title.substringAfter(". Part").trim()

        Session(
            first.id,
            title,
            parts.joinToString("\n") { it.description },
            parts.flatMap { it.speakerIds },
            first.location,
            startTime,
            endTime,
            first.tags
        )
    }

    return nonWorkshop + workshops
}
