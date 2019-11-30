package org.jetbrains.kotlinconf.presentation

import org.jetbrains.kotlinconf.*

fun List<SessionModel>.filter(searchQuery: String?): List<SessionModel> {
    searchQuery?.takeUnless { it.isEmpty() } ?: return this
    val searchQueryLower = searchQuery.toLowerCase()
    return filter { session ->
        searchQueryLower in session.title.toLowerCase() || session.speakers.map { it.fullName }.any { fullName ->
            searchQueryLower in fullName.toLowerCase()
        }
    }
}