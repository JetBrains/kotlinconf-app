package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

// This format is enforced by Sessionize and it should not be changed unless we extract Sessionize DTO
@Serializable
data class AllData(
    @Optional
    val sessions: List<Session> = emptyList(),
    @Optional
    val rooms: List<Room> = emptyList(),
    @Optional
    val speakers: List<Speaker> = emptyList(),
    @Optional
    val questions: List<Question> = emptyList(),
    @Optional
    val categories: List<Category> = emptyList(),
    @Optional
    val favorites: List<Favorite> = emptyList(),
    @Optional
    val votes: List<Vote> = emptyList()
)

class SessionizeData(val allData: AllData, val etag: String = allData.hashCode().toString())
