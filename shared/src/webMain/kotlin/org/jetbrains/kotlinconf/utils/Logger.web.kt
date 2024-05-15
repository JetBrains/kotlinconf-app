package org.jetbrains.kotlinconf.utils

import io.ktor.client.plugins.logging.Logger

// TODO rename
actual fun appLogger(): Logger = object : Logger {
    override fun log(message: String) {
        println("LOG: " + message)
    }
}