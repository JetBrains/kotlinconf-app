package org.jetbrains.kotlinconf.utils

inline fun <T, R> List<T>.performSearch(
    searchText: String,
    produceResult: (T, List<List<IntRange>>) -> R,
    selectors: List<(T) -> String>,
): List<R> {
    val diacriticsSearch = searchText.containsDiacritics()
    val searchPattern = searchText.toRegex(RegexOption.IGNORE_CASE)

    return this.mapNotNull { item ->
        val allMatches = selectors.map { selector ->
            val field = selector(item)
            searchPattern
                .findAll(if (diacriticsSearch) field else field.removeDiacritics())
                .map(MatchResult::range)
                .toList()
        }
        if (allMatches.any { it.isNotEmpty() }) {
            produceResult(item, allMatches)
        } else {
            null
        }
    }
}
