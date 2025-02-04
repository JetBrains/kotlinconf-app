package org.jetbrains.kotlinconf

import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimeProvider(private val client: APIClient) {
    private var serverTime = GMTDate()
    private var requestTime = GMTDate()

    fun now(): GMTDate = GMTDate() + (serverTime.timestamp - requestTime.timestamp)

    private val _time = MutableStateFlow(GMTDate())
    val time: StateFlow<GMTDate> = _time.asStateFlow()

    suspend fun run(): Nothing {
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
