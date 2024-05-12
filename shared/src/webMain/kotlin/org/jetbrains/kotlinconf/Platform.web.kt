package org.jetbrains.kotlinconf

fun randomUUID(): String = js("crypto.randomUUID()")
