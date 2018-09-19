package org.jetbrains.kotlinconf.presentation


interface SearchQueryProvider {
    val searchQuery: String
    fun addOnQueryChangedListener(listener: (String) -> Unit)
}