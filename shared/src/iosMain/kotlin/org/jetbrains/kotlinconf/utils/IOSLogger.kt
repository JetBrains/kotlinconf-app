package org.jetbrains.kotlinconf.utils

class IOSLogger : Logger {
    override fun log(tag: String, message: String) {
        println("[$tag] $message")
    }
}
