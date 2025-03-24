package org.jetbrains.kotlinconf.utils

import doist.x.normalize.Form
import doist.x.normalize.normalize

private val diacricitsRegex = "\\p{Mn}+".toRegex()

fun String.containsDiacritics(): Boolean = normalizeNfd().contains(diacricitsRegex)

fun String.removeDiacritics() = normalizeNfd().replace(diacricitsRegex, "")

private fun String.normalizeNfd(): String = normalize(Form.NFD)
