package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

// This format is enforced by Sessionize and it should not be changed unless we extract Sessionize DTO
@Serializable
data class Question(
    val question: String,
    val id: Int,
    val sort: Int,
    val questionType: String
)
