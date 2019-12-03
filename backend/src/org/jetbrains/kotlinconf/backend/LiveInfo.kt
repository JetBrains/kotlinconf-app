package org.jetbrains.kotlinconf.backend

import org.jetbrains.kotlinconf.*
import java.util.concurrent.locks.*
import kotlin.concurrent.*

private val live = mutableMapOf<Int, String>()
private val lock = ReentrantReadWriteLock()

internal fun liveInfo(): List<LiveVideo> = lock.read {
    live.map { LiveVideo(it.key, it.value) }
}

internal fun addLive(room: Int, video: String? = null) {
    lock.write {
        if (video == null) {
            live.remove(room)
        } else {
            live[room] = video
        }
    }
}