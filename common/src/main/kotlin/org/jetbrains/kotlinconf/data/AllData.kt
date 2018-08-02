package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class AllData(
    val sessions: List<Session>? = null,
    val rooms: List<Room>? = null,
    val speakers: List<Speaker>? = null,
    val questions: List<Question>? = null,
    val categories: List<Category>? = null,
    val favorites: List<Favorite>? = null,
    val votes: List<Vote>? = null
)

class SessionizeData(val allData: AllData, val etag: String = allData.hashCode().toString())