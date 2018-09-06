package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Category(
    val id: Int,
    val sort: Int,
    val title: String,
    val items: List<CategoryItem>
)
