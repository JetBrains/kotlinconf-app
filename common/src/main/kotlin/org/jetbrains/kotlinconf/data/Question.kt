package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Question(
    val question: String? = null,
    val id: Int? = null,
    val sort: Int? = null,
    val questionType: String? = null
)
