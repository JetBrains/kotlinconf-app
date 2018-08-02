package org.jetbrains.kotlinconf.data

//@Serializable
data class Category(
    val id: Int,
    val sort: Int,
    val title: String,
    val items: List<CategoryItem>
)
