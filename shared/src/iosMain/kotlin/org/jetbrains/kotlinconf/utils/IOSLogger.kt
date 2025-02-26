package org.jetbrains.kotlinconf.utils

class IOSLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        println("[$tag] ${lazyMessage()}")
    }
}
