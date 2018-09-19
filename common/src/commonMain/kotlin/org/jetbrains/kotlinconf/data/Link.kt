package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Link(
    val linkType: String,
    val title: String,
    val url: String
)
