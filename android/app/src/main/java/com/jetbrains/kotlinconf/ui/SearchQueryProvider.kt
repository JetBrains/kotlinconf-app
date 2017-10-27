package org.jetbrains.kotlinconf.ui


interface SearchQueryProvider {
    val searchQuery: String
    fun addOnQueryChangedListener(listener: (String) -> Unit)
}