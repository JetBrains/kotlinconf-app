package org.jetbrains.kotlinconf.data

//@Serializable
data class Question(
    val question: String,
    val id: Int,
    val sort: Int,
    val questionType: String
)
