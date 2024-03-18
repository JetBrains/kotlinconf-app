package org.jetbrains.kotlinconf.utils

import io.ktor.client.plugins.logging.Logger

actual fun mobileLogger(): Logger = object : Logger {
    override fun log(message: String) {
        println(message)
    }
}