package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Speaker(
    val firstName: String? = null,
    val lastName: String? = null,
    val profilePicture: String? = null,
    val sessions: List<Int?>? = null,
    val tagLine: String? = null,
    val isTopSpeaker: Boolean? = null,
    val bio: String? = null,
    val fullName: String? = null,
    val links: List<Link?>? = null,
    val id: String? = null
)
