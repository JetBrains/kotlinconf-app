package org.jetbrains.kotlinconf.data


enum class SessionRating(val value: Int) {
    BAD(-1),
    OK(0),
    GOOD(1);

    companion object {
        fun valueOf(value: Int): SessionRating = SessionRating.values().find { it.value == value }!!
    }
}