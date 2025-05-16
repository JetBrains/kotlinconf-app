package org.jetbrains.kotlinconf.backend.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.SpeakerId

@Serializable
class SessionizeData(
    val sessions: List<SessionData> = emptyList(),
    val rooms: List<RoomData> = emptyList(),
    val speakers: List<SpeakerData> = emptyList(),
    val questions: List<QuestionData> = emptyList(),
    val categories: List<CategoryData> = emptyList(),
    val partners: List<PartnerData> = emptyList()
)

@Serializable
data class SessionData(
    val id: SessionId,
    val isServiceSession: Boolean,
    val isPlenumSession: Boolean,
    val speakers: List<SpeakerId>,
    @SerialName("description")
    var descriptionText: String? = "",
    val startsAt: Instant?,
    val endsAt: Instant?,
    val title: String,
    val roomId: Int?,
    val questionAnswers: List<QuestionAnswerData> = emptyList(),
    val categoryItems: List<Int> = emptyList()
) {
    val displayTitle: String = title.trim()

    init {
        if (descriptionText == null) descriptionText = ""
    }
}

@Serializable
class RoomData(
    val name: String,
    val id: Int,
    val sort: Int
)

@Serializable
class SpeakerData(
    val id: SpeakerId,
    val firstName: String,
    val lastName: String,
    val profilePicture: String?,
    val sessions: List<String>,
    val tagLine: String?,
    val isTopSpeaker: Boolean,
    val bio: String?,
    val fullName: String,
    val links: List<LinkData> = emptyList(),
    val categoryItems: List<Int> = emptyList(),
    val questionAnswers: List<QuestionAnswerData> = emptyList()
)

@Serializable
class QuestionData(
    val question: String,
    val id: Int,
    val sort: Int,
    val questionType: String
)

@Serializable
class CategoryData(
    val id: Int,
    val sort: Int,
    val title: String,
    val items: List<CategoryItemData> = emptyList()
)

@Serializable
class QuestionAnswerData(
    val questionId: Int,
    val answerValue: String
)

@Serializable
class LinkData(
    val linkType: String,
    val title: String,
    val url: String
)

@Serializable
class CategoryItemData(
    val name: String,
    val id: Int,
    val sort: Int
)

@Serializable
class PartnerData(
    val name: String,
    val logo: String,
    val description: String
)

@Serializable
class LiveVideo(val room: Int, val videoId: String)
