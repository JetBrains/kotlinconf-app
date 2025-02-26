package org.jetbrains.kotlinconf.utils

interface Logger {
    fun log(tag: String, message: String)
}

class NoopProdLogger : Logger {
    override fun log(tag: String, message: String) {
        // No logging in prod
    }
}
