package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Link(
    val linkType: String? = null,
    val title: String? = null,
    val url: String? = null
)
