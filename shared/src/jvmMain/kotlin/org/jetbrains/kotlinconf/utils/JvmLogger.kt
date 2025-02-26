package org.jetbrains.kotlinconf.utils

class JvmLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        println("[$tag] ${lazyMessage()}")
    }
}
