package org.jetbrains.kotlinconf.data

import kotlinx.serialization.Serializable

@Serializable
data class QuestionAnswer(
    val questionId: Int,
    val answerValue: String
)
