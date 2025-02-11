package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate

enum class SessionState {
    Live,
    Past,
    Upcoming,
    ;

    companion object {
        fun from(startsAt: GMTDate, endsAt: GMTDate, now: GMTDate): SessionState = when {
            startsAt <= now && now < endsAt -> Live
            endsAt <= now -> Past
            else -> Upcoming
        }
    }
}
