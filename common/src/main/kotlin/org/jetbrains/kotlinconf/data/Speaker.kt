package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

// This format is enforced by Sessionize and it should not be changed unless we extract Sessionize DTO
@Serializable
data class Speaker(
    val firstName: String,
    val lastName: String,
    val profilePicture: String?,
    val sessions: List<Int>,
    val tagLine: String,
    val isTopSpeaker: Boolean,
    val bio: String,
    val fullName: String,
    val links: List<Link>,
    val id: String
)
