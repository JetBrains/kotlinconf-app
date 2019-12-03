package org.jetbrains.kotlinconf

import kotlinx.serialization.*

@Serializable
class FeedData(
    val statuses: List<FeedPost> = emptyList()
)

@Serializable
class FeedPost(
    val id_str: String,
    val created_at: String,
    val text: String,
    val user: FeedUser,
    val entities: FeedEntities
)

@Serializable
class FeedUser(
    val id_str: String,
    val name: String,
    val profile_image_url_https: String,
    val screen_name: String
)

@Serializable
class FeedEntities(
    val media: List<FeedMedia> = emptyList()
)

@Serializable
class FeedMedia(
    val media_url: String? = null,
    val media_url_https: String? = null,
    val type: String? = null
)