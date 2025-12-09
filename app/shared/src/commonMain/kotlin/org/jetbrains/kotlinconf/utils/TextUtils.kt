package org.jetbrains.kotlinconf.utils

import doist.x.normalize.Form
import doist.x.normalize.normalize

private val diacriticsRegex = "\\p{Mn}+".toRegex()

fun String.containsDiacritics(): Boolean = normalizeNfd().contains(diacriticsRegex)

fun String.removeDiacritics() = normalizeNfd().replace(diacriticsRegex, "")

private fun String.normalizeNfd(): String = normalize(Form.NFD)
