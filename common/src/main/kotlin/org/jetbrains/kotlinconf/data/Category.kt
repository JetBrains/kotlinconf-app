package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

@Serializable
data class Category(
    val id: Int? = null,
    val sort: Int? = null,
    val title: String? = null,
    val items: List<CategoryItem?>? = null
)
