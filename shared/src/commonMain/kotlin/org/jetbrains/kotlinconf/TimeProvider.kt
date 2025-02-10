package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate
import io.ktor.util.date.Month
import io.ktor.util.date.plus
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface TimeProvider {
    fun now(): GMTDate
    val time: StateFlow<GMTDate>
    suspend fun run(): Nothing
}

class ServerBasedTimeProvider(private val client: APIClient) : TimeProvider {
    private var serverTime = GMTDate()
    private var requestTime = GMTDate()

    override fun now(): GMTDate = GMTDate() + (serverTime.timestamp - requestTime.timestamp)

    private val _time = MutableStateFlow(GMTDate())
    override val time: StateFlow<GMTDate> = _time.asStateFlow()

    override suspend fun run(): Nothing {
        runCatching {
            serverTime = client.getServerTime()
            requestTime = GMTDate()
        }
        _time.value = now()

        while (true) {
            delay(60_000)
            _time.value = now()
        }
    }
}

class FakeTimeProvider(
    private val baseTime: GMTDate = GMTDate(
        year = 2024, month = Month.MAY, dayOfMonth = 23, hours = 12, minutes = 40, seconds = 0
    ),
    private val freezeTime: Boolean = false,
) : TimeProvider {
    private val _time = MutableStateFlow(baseTime)
    override val time: StateFlow<GMTDate> = _time
    override fun now(): GMTDate = baseTime
    override suspend fun run(): Nothing {
        if (freezeTime) {
            awaitCancellation()
        } else {
            while (true) {
                // Progress time at 4x speed for testing
                delay(15.seconds)
                _time.update { t ->
                    t.plus(1.minutes).also {
                        println("Fake time is now $it")
                    }
                }
            }
        }
    }
}
