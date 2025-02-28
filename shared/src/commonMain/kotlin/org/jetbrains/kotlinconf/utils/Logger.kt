package org.jetbrains.kotlinconf.utils

interface Logger {
    fun log(tag: String, lazyMessage: () -> String)
}

class NoopProdLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        // No logging in prod
    }
}
