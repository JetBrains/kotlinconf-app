package org.jetbrains.kotlinconf.backend

import com.github.salomonbrys.kotson.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import kotlinx.coroutines.*
import org.jetbrains.kotlinconf.data.*
import java.net.*
import java.text.*
import java.time.*
import java.util.*
import java.util.concurrent.*

val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

@Volatile
private var sessionizeData: SessionizeData? = null
val comeBackLater = HttpStatusCode(477, "Come Back Later")
val tooLate = HttpStatusCode(478, "Too Late")
val keynoteTimeZone = ZoneId.of("Europe/Paris")!!
val keynoteEndDateTime = ZonedDateTime.of(2018, 10, 4, 10, 0, 0, 0, keynoteTimeZone)!!

const val fakeSessionId = "007"

fun Application.launchSyncJob(sessionizeUrl: String, sessionizeInterval: Long) {
    log.info("Synchronizing each $sessionizeInterval minutes with $sessionizeUrl")
    launch(CommonPool) {
        while (true) {
            log.trace("Synchronizing to Sessionize…")
            synchronizeWithSessionize(sessionizeUrl)
            log.trace("Finished loading data from Sessionize.")
            delay(sessionizeInterval, TimeUnit.MINUTES)
        }
    }
}

suspend fun synchronizeWithSessionize(sessionizeUrl: String) {
    val client = HttpClient()
    val response = client.call(URL(sessionizeUrl)) {}
    val text = response.receive<String>()
    val data = gson.fromJson<AllData>(text)
    sessionizeData = SessionizeData(data)
}

fun getSessionizeData() = sessionizeData ?: throw ServiceUnavailable()