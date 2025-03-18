package org.jetbrains.kotlinconf.utils

actual fun String.normalizeNfd(): String = normalizeImpl(this)

private fun normalizeImpl(str: String): String = js("""str.normalize("NFD")""")
