package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Room(
    val name: String? = null,
    val id: Int? = null,
    val sort: Int? = null
)
