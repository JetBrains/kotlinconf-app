package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Session(
    val id: String? = null,
    val isServiceSession: Boolean? = null,
    val isPlenumSession: Boolean? = null,
    val questionAnswers: List<QuestionAnswer?>? = null,
    val speakers: List<String?>? = null,
    val description: String? = null,
    val startsAt: String? = null,
    val title: String? = null,
    val endsAt: String? = null,
    val categoryItems: List<Int?>? = null,
    val roomId: Int? = null
)
