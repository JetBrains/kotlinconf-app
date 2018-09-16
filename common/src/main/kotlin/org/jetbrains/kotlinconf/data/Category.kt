package org.jetbrains.kotlinconf.data

import kotlinx.serialization.*

// This format is enforced by Sessionize and it should not be changed unless we extract Sessionize DTO
@Serializable
data class Category(
    val id: Int,
    val sort: Int,
    val title: String,
    val items: List<CategoryItem>
)
