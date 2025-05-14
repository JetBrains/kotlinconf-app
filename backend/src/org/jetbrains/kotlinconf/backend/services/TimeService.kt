package org.jetbrains.kotlinconf.backend.services

import io.ktor.util.date.*

private val GMT_TIME_OFFSET = 2 * 60 * 60 * 1000

class TimeService {

    @Volatile
    private var simulatedTime: GMTDate? = null

    @Volatile
    private var updatedTime: GMTDate = GMTDate()

    fun updateTime(time: GMTDate?) {
        simulatedTime = time
        updatedTime = GMTDate()
    }

    fun now(): Long {
        val start = simulatedTime

        return if (start == null) {
            return GMTDate().timestamp + GMT_TIME_OFFSET
        } else {
            val offset = GMTDate().timestamp - updatedTime.timestamp
            (start + offset).timestamp
        }
    }

}