package org.jetbrains.kotlinconf.utils

/**
 * No normalize support for web targets for now. Returns the original string.
 */
actual fun String.normalizeNfd(): String = this
