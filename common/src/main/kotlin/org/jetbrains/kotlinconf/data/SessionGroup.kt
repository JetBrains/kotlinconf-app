package org.jetbrains.kotlinconf.data

//@Serializable
data class SessionGroup(
    val groupName: String,
    val sessions: List<Session>,
    val groupId: Int
)
