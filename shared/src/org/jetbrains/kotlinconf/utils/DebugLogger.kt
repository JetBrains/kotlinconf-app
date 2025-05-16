package org.jetbrains.kotlinconf.utils

import kotlinx.datetime.Clock

class DebugLogger(private val platformLogger: Logger) : Logger {
    private val logs = mutableListOf<String>()

    override fun log(tag: String, lazyMessage: () -> String) {
        logs += "${Clock.System.now()} [${tag}] ${lazyMessage()}"
        platformLogger.log(tag, lazyMessage)
    }

    fun getAllLogs(): String = logs.joinToString("\n")
}
