package org.jetbrains.kotlinconf.data

import kotlinx.serialization.Serializable

@Serializable
data class CategoryItem(
    val name: String,
    val id: Int,
    val sort: Int
)
