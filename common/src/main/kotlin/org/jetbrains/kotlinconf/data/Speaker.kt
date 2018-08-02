package org.jetbrains.kotlinconf.data

//@Serializable
data class Speaker(
    val firstName: String,
    val lastName: String,
    val profilePicture: String,
    val sessions: List<Int>,
    val tagLine: String,
    val isTopSpeaker: Boolean,
    val bio: String,
    val fullName: String,
    val links: List<Link>,
    val id: String
)
