package org.jetbrains.kotlinconf.utils

import io.ktor.client.plugins.logging.Logger

actual object MobileLogger : Logger {
    override fun log(message: String) {
        println(message)
    }
}