package org.jetbrains.kotlinconf.data

//@Serializable
data class AllData(
    val sessions: List<Session> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val speakers: List<Speaker> = emptyList(),
    val questions: List<Question> = emptyList(),
    val categories: List<Category> = emptyList(),
    val favorites: List<Favorite> = emptyList(),
    val votes: List<Vote> = emptyList()
)

class SessionizeData(val allData: AllData, val etag: String = allData.hashCode().toString())
