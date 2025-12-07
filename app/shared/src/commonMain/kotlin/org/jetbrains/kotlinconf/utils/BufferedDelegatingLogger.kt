package org.jetbrains.kotlinconf.utils

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A logger that's constructed during startup and later attached to a real logger.
 * Until attached, it buffers log entries in memory. When [attach] is called,
 * it forwards the buffered entries to the attached logger in order, and from
 * that point on it delegates all calls directly.
 */
@Inject
@SingleIn(AppScope::class)
class BufferedDelegatingLogger(
    private val scope: CoroutineScope,
) : Logger {
    private var delegate: Logger? = null

    private data class Entry(val tag: String, val lazyMessage: () -> String)

    private val mutex = Mutex()

    private val buffer = mutableListOf<Entry>()

    override fun log(tag: String, lazyMessage: () -> String) {
        val current = delegate
        if (current != null) {
            current.log(tag, lazyMessage)
            return
        }

        scope.launch {
            mutex.withLock {
                buffer += Entry(tag, lazyMessage)
                while (buffer.size > MAX_LOG_MESSAGES_IN_MEMORY) {
                    buffer.removeAt(0)
                }
            }
        }
    }

    fun attach(realLogger: Logger) {
        require(delegate == null) { "Logger delegate was already set, this should only happen once" }

        delegate = realLogger

        scope.launch {
            mutex.withLock {
                buffer.forEach { entry ->
                    realLogger.log(entry.tag, entry.lazyMessage)
                }
                buffer.clear()
            }
        }
    }
}
