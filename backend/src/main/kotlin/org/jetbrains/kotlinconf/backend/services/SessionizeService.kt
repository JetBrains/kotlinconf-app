package org.jetbrains.kotlinconf.backend.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.Session
import org.jetbrains.kotlinconf.Speaker
import org.jetbrains.kotlinconf.backend.model.CategoryItemData
import org.jetbrains.kotlinconf.backend.model.SessionizeData
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class SessionizeService(
    private val client: HttpClient,
    config: ConferenceConfig
): Closeable {
    private val conference = MutableSharedFlow<Conference>(replay = 1)
    private val sessionizeUrl: String = config.sessionizeUrl
    private val sessionizeInterval: Long = config.sessionizeInterval
    private val log = LoggerFactory.getLogger("SessionizeService")

    @OptIn(DelicateCoroutinesApi::class)
    private val syncJob = GlobalScope.launch {
        log.info("Synchronizing each $sessionizeInterval minutes with $sessionizeUrl")
        while (true) {
            log.trace("Synchronizing to Sessionize…")
            synchronizeWithSessionize(sessionizeUrl)
            log.trace("Finished loading data from Sessionize.")
            delay(TimeUnit.MINUTES.toMillis(sessionizeInterval))
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

    suspend fun fetchImage(
        imagesUrl: String,
        imageId: String
    ): ByteArray {
        return client.get("$imagesUrl/$imageId").body<ByteArray>()
    }

    suspend fun getConferenceData(): Conference = conference.first()

    private fun SessionizeData.toConference(): Conference {
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

    private fun List<Session>.mergeWorkshops(): List<Session> {
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

    override fun close() {
        syncJob.cancel()
    }
}