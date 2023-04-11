package org.jetbrains.kotlinconf.backend

import io.ktor.util.date.*

@Volatile
private var simulatedTime: GMTDate? = null

@Volatile
private var updatedTime: GMTDate = GMTDate()

internal fun updateTime(time: GMTDate?) {
    simulatedTime = time
    updatedTime = GMTDate()
}

internal fun now(): Long {
    val start = simulatedTime

    return if (start == null) {
        return GMTDate().timestamp + GMT_TIME_OFFSET
    } else {
        val offset = GMTDate().timestamp - updatedTime.timestamp
        (start + offset).timestamp
    }
}
