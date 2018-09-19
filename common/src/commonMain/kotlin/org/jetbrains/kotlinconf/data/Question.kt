package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Question(
    val question: String,
    val id: Int,
    val sort: Int,
    val questionType: String
)
