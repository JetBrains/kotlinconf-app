package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlinx.serialization.*
import org.jetbrains.kotlinconf.data.*

@Serializable
class SessionModel(
    val id: String,
    val title: String,
    val category: String?,
    val descriptionText: String,
    val startsAtStr: String?,
    val endsAtStr: String?,
    val room: String?,
    val speakers: List<Speaker>
) {
    @Transient
    val startsAt: GMTDate?
        get() = startsAtStr?.parseDate()

    @Transient
    val endsAt: GMTDate?
        get() = endsAtStr?.parseDate()

    companion object {
        fun forSession(all: AllData, sessionId: String): SessionModel {
            val briefSession = all.sessions.first { it.id == sessionId }
            val speakerMap = all.speakers.associateBy { it.id }
            val roomMap = all.rooms.associateBy { it.id }
            val categoryMap = all.categories
                .flatMap { it.items }
                .associateBy { it.id }

            return forSession(briefSession,
                speakerProvider = { id -> speakerMap[id] },
                categoryProvider = { id -> categoryMap[id] },
                roomProvider = { id -> roomMap[id]!! }
            )
        }

        private fun forSession(
            briefSession: Session,
            speakerProvider: (String) -> Speaker?,
            categoryProvider: (Int) -> CategoryItem?,
            roomProvider: (Int) -> Room
        ): SessionModel {
            val startsAt = briefSession.startsAt
            val endsAt = briefSession.endsAt

            return SessionModel(
                id = briefSession.id,
                title = briefSession.title,
                category = briefSession.categoryItems.map(categoryProvider).firstOrNull()?.name,
                descriptionText = briefSession.descriptionText ?: "",
                startsAtStr = startsAt,
                endsAtStr = endsAt,
                speakers = briefSession.speakers.mapNotNull { speakerProvider(it) },
                room = briefSession.roomId?.let(roomProvider)?.name
            )
        }
    }
}

fun AllData.allSessions(): List<SessionModel> = sessions
    .map { SessionModel.forSession(this, it.id) }
    .sorted()

fun AllData.favoriteSessions(): List<SessionModel> = favorites
    .map { it.sessionId }
    .map { SessionModel.forSession(this, it) }
    .sorted()

private fun List<SessionModel>.sorted(): List<SessionModel> =
    sortedWith(compareBy({ it.startsAt?.timestamp }, { it.title }))
