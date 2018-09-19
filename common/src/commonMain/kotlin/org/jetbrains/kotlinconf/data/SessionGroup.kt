package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class SessionGroup(
    val groupName: String,
    val sessions: List<Session>,
    val groupId: Int
)
