package org.jetbrains.kotlinconf

import io.ktor.util.date.*
import kotlin.time.*

/**
 * Conference start time in GMT.
 */
val CONFERENCE_START = GMTDate(
    0, 0, 9, 5, Month.DECEMBER, 2019
)

/**
 * Conference end time in GMT.
 */
val CONFERENCE_END = GMTDate(
    1, 0, 0, 7, Month.DECEMBER, 2019
)

/**
 * Votes count to get t-shirt.
 */
val VOTES_FOR_TSHIRT = 10


sealed class HomeState {
    @UseExperimental(ExperimentalTime::class)
    class Before(private val duration: Duration) : HomeState() {
        var seconds: Int = 0
            private set

        var minutes: Int = 0
            private set

        var hours: Int = 0
            private set

        var days: Int = 0
            private set

        init {
            duration.toComponents { day, hour, minute, second, _ ->
                days = day
                hours = hour
                minutes = minute
                seconds = second
            }
        }

        fun isEmpty(): Boolean = duration.inSeconds <= 0

        override fun equals(other: Any?): Boolean = other != null && other is Before
    }

    object During : HomeState()
    object After : HomeState()
}

