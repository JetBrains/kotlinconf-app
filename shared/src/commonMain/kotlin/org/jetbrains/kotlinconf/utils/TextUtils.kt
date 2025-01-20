package org.jetbrains.kotlinconf.utils

private val diacricitsRegex = "\\p{Mn}+".toRegex()

fun String.containsDiacritics(): Boolean = normalizeNfd().contains(diacricitsRegex)

fun String.removeDiacritics() = normalizeNfd().replace(diacricitsRegex, "")

/**
 * Normalize the string to the NDF normal form.
 */
expect fun String.normalizeNfd(): String
