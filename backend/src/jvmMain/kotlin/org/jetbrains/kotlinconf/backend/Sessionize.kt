package org.jetbrains.kotlinconf.backend

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.date.*
import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.*
import java.time.*
import java.util.*
import java.util.concurrent.*

@Volatile
private var conference: Conference? = null

val comeBackLater = HttpStatusCode(477, "Come Back Later")
val tooLate = HttpStatusCode(478, "Too Late")
val zoneId: ZoneId = ZoneId.of("Europe/Amsterdam")

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
    conference = client.get(sessionizeUrl)
        .body<SessionizeData>()
        .toConference()
}

fun getSessionizeData(): Conference = conference ?: throw ServiceUnavailable()

fun SessionizeData.toConference(): Conference {
    fun findRoom(id: Int) =  rooms.find { it.id == id }?.name ?: "unknown"
    val sessions = sessions.mapNotNull { it ->
        val startsAt = it.startsAt ?: return@mapNotNull null
        val endsAt = it.endsAt ?: return@mapNotNull null
        Session(
            it.id,
            it.displayTitle,
            it.descriptionText ?: "",
            it.speakers,
            it.roomId?.let { findRoom(it) } ?: "unknown",
            startsAt,
            endsAt
        )
    }

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