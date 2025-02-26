package org.jetbrains.kotlinconf.utils

class JvmLogger : Logger {
    override fun log(tag: String, message: String) {
        println("[$tag] $message")
    }
}
