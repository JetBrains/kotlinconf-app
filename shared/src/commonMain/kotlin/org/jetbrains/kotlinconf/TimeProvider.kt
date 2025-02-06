package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate
import io.ktor.util.date.Month
import io.ktor.util.date.plus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val fixedTime: GMTDate = GMTDate(
        year = 2024, month = Month.MAY, dayOfMonth = 23, hours = 13, minutes = 40, seconds = 0
    )
) : TimeProvider {
    override val time: StateFlow<GMTDate> = MutableStateFlow(fixedTime)
    override fun now(): GMTDate = fixedTime
    override suspend fun run(): Nothing {
        do while (true)
    }
}
