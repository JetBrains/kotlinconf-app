package org.jetbrains.kotlinconf

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.kotlinconf.utils.Logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface TimeProvider {
    fun now(): LocalDateTime
    val time: StateFlow<LocalDateTime>
    suspend fun run(): Nothing

    fun getNotificationTime(notificationTime: LocalDateTime): LocalDateTime = notificationTime
    fun getNotificationDelay(notificationTime: LocalDateTime): Duration = notificationTime - now()
}

val EVENT_TIME_ZONE = TimeZone.of("Europe/Copenhagen")

operator fun LocalDateTime.minus(other: LocalDateTime): Duration =
    toInstant(EVENT_TIME_ZONE) - other.toInstant(EVENT_TIME_ZONE)

operator fun LocalDateTime.minus(duration: Duration): LocalDateTime =
    (toInstant(EVENT_TIME_ZONE) - duration).toLocalDateTime(EVENT_TIME_ZONE)

class ServerBasedTimeProvider(private val client: APIClient) : TimeProvider {
    private var offset: Duration = Duration.ZERO

    override fun now(): LocalDateTime {
        return (Clock.System.now() + offset).toLocalDateTime(EVENT_TIME_ZONE)
    }

    private val _time = MutableStateFlow(Clock.System.now().toLocalDateTime(EVENT_TIME_ZONE))
    override val time: StateFlow<LocalDateTime> = _time.asStateFlow()

    override suspend fun run(): Nothing {
        runCatching {
            val serverTime = client.getServerTime()
            if (serverTime != null) {
                val requestTime = Clock.System.now()
                offset = serverTime - requestTime
            }
        }
        _time.value = now()

        while (true) {
            delay(60_000)
            _time.value = now()
        }
    }
}

class FakeTimeProvider(
    private val logger: Logger,
    baseTime: LocalDateTime = LocalDateTime.parse("2025-05-22T14:30:00"),
    private val freezeTime: Boolean = false,
    private val speedMultiplier: Double = 20.0,
) : TimeProvider {
    private val _time = MutableStateFlow(baseTime)
    override val time: StateFlow<LocalDateTime> = _time
    override fun now(): LocalDateTime = _time.value
    override suspend fun run(): Nothing {
        if (freezeTime) {
            awaitCancellation()
        } else {
            while (true) {
                delay((60.0 / speedMultiplier).seconds)
                _time.update { t ->
                    t.toInstant(EVENT_TIME_ZONE)
                        .plus(1.minutes)
                        .toLocalDateTime(EVENT_TIME_ZONE)
                        .also {
                            logger.log("FakeTimeProvider") { "Fake time is now $it" }
                        }
                }
            }
        }
    }

    override fun getNotificationTime(notificationTime: LocalDateTime): LocalDateTime =
        (Clock.System.now() + getNotificationDelay(notificationTime)).toLocalDateTime(EVENT_TIME_ZONE)

    override fun getNotificationDelay(notificationTime: LocalDateTime): Duration =
        (notificationTime - now()) / speedMultiplier
}
