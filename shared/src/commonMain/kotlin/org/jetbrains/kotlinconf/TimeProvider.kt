package org.jetbrains.kotlinconf

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface TimeProvider {
    fun now(): LocalDateTime
    val time: StateFlow<LocalDateTime>
    suspend fun run(): Nothing
}

val EVENT_TIME_ZONE = TimeZone.of("Europe/Copenhagen")

operator fun LocalDateTime.minus(other: LocalDateTime): Duration =
    toInstant(EVENT_TIME_ZONE) - other.toInstant(EVENT_TIME_ZONE)

class ServerBasedTimeProvider(private val client: APIClient) : TimeProvider {
    private var serverTime: Instant = Clock.System.now()
    private var offset: Duration = Duration.ZERO

    override fun now(): LocalDateTime {
        return (Clock.System.now() + offset).toLocalDateTime(EVENT_TIME_ZONE)
    }

    private val _time = MutableStateFlow(Clock.System.now().toLocalDateTime(EVENT_TIME_ZONE))
    override val time: StateFlow<LocalDateTime> = _time.asStateFlow()

    override suspend fun run(): Nothing {
        runCatching {
            serverTime = client.getServerTime()
            val requestTime = Clock.System.now()
            offset = serverTime - requestTime
        }
        _time.value = now()

        while (true) {
            delay(60_000)
            _time.value = now()
        }
    }
}

class FakeTimeProvider(
    private val baseTime: LocalDateTime = LocalDateTime.parse("2024-05-23T12:40:00"),
    private val freezeTime: Boolean = false,
) : TimeProvider {
    private val _time = MutableStateFlow(baseTime)
    override val time: StateFlow<LocalDateTime> = _time
    override fun now(): LocalDateTime = baseTime
    override suspend fun run(): Nothing {
        if (freezeTime) {
            awaitCancellation()
        } else {
            while (true) {
                // Progress time at 4x speed for testing
                delay(15.seconds)
                _time.update { t ->
                    t.toInstant(EVENT_TIME_ZONE)
                        .plus(1.minutes)
                        .toLocalDateTime(EVENT_TIME_ZONE)
                        .also {
                            println("Fake time is now $it")
                        }
                }
            }
        }
    }
}
