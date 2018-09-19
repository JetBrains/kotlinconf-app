package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Room(
    val name: String,
    val id: Int,
    val sort: Int
)
