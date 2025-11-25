@file:OptIn(ExperimentalTime::class)

package org.jetbrains.kotlinconf.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class DebugLogger(private val platformLogger: Logger) : Logger {
    private val logs = mutableListOf<String>()

    override fun log(tag: String, lazyMessage: () -> String) {
        logs += "${Clock.System.now()} [${tag}] ${lazyMessage()}"
        while (logs.size > MAX_LOG_MESSAGES_IN_MEMORY) {
            logs.removeAt(0)
        }
        platformLogger.log(tag, lazyMessage)
    }

    fun getAllLogs(): String = logs.joinToString("\n")
}
