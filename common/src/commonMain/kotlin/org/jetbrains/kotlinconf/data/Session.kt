package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Session(
    val id: String,
    val isServiceSession: Boolean,
    val isPlenumSession: Boolean,
    val questionAnswers: List<QuestionAnswer>,
    val speakers: List<String>,
    val descriptionText: String?,
    val startsAt: String,
    val title: String,
    val endsAt: String,
    val categoryItems: List<Int>,
    val roomId: Int
)
