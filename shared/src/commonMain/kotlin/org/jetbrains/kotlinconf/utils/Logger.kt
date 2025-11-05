package org.jetbrains.kotlinconf.utils

interface Logger {
    fun log(tag: String, lazyMessage: () -> String)
}

fun Logger.tagged(tag: String) = TaggedLogger(tag, this)

class TaggedLogger(
    private val tag: String,
    private val delegate: Logger,
) {
    fun log(lazyMessage: () -> String) {
        delegate.log(tag, lazyMessage)
    }
}

class NoopProdLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        // No logging in prod
    }
}

/**
 * A logger that's constructed during startup and later attached to a real logger.
 * Until attached, it buffers log entries in memory. When [attach] is called,
 * it forwards the buffered entries to the attached logger in order, and from
 * that point on it delegates all calls directly.
 */
class BufferedDelegatingLogger : Logger {
    private var delegate: Logger? = null

    private data class Entry(val tag: String, val message: String)

    private val buffer = mutableListOf<Entry>()

    override fun log(tag: String, lazyMessage: () -> String) {
        val current = delegate
        if (current != null) {
            current.log(tag, lazyMessage)
            return
        }

        buffer += Entry(tag, lazyMessage())
    }

    fun attach(realLogger: Logger) {
        require(delegate == null) { "Logger delegate was already set, this should only happen once" }

        buffer.forEach { entry ->
            realLogger.log(entry.tag) { entry.message }
        }
        buffer.clear()
        delegate = realLogger
    }
}
