package org.jetbrains.kotlinconf.backend

import io.ktor.util.date.*

@Volatile
private var simulatedTime: GMTDate? = null

@Volatile
private var updatedTime: GMTDate = GMTDate()

@Volatile
internal var votesRequired = 10


internal fun updateTime(time: GMTDate?) {
    simulatedTime = time
    updatedTime = GMTDate()
}

internal fun now(): GMTDate {
    val start = simulatedTime

    return if (start == null) {
        GMTDate()
    } else {
        val offset = GMTDate().timestamp - updatedTime.timestamp
        start + offset
    }
}
