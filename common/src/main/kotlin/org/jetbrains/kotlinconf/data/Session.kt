package org.jetbrains.kotlinconf.data

//@Serializable
data class Session(
    val id: String,
    val isServiceSession: Boolean,
    val isPlenumSession: Boolean,
    val questionAnswers: List<QuestionAnswer>,
    val speakers: List<String>,
    val description: String,
    val startsAt: String,
    val title: String,
    val endsAt: String,
    val categoryItems: List<Int>,
    val roomId: Int
)
