package org.jetbrains.kotlinconf.backend

import com.github.salomonbrys.kotson.*
import org.jetbrains.kotlinconf.data.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import kotlinx.coroutines.experimental.*
import java.net.*
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.Date

val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

@Volatile
var sessionizeData: SessionizeData? = null
val comeBackLater = HttpStatusCode(477, "Come Back Later")
val tooLate = HttpStatusCode(478, "Too Late")
val keynoteTimeZone = ZoneId.of("America/Los_Angeles")
val keynoteEndDateTime = ZonedDateTime.of(2017, 11, 2, 10, 0, 0, 0, keynoteTimeZone)

val fakeSessionId = "007"

val fakeVotingSession = Session(
        id = fakeSessionId,
        title = "The Other Kotlin",
        description = """Kotlin is an island located near the head of the Gulf of Finland. With an area of 15 square kilometers (that’s roughly 9.3 square miles for non-metric folks), and a population of 43.000 people (apparently nobody has bothered updating the census since 2010), Kotlin has recently piqued a lot of interest based on Google trends. In this talk we’re going to give the perspective of Kotlin’s population and how this sudden interest has impacted their lives. We’ll be sharing stories of local residents, as well as those that have traveled from other destinations to take a picture of the now well-known Kotlin lighthouse. Spend 60 minutes with us to learn about the fascinating (really?) island of Kotlin.""",
        startsAt = apiDateFormat.format(Date(1509613200000)),
        endsAt = apiDateFormat.format(Date(1509616800000)),
        speakers = listOf("9671b9b6-771a-4df2-b800-1298c43b0a3b"),
        categoryItems = null,
        isServiceSession = false,
        isPlenumSession = true,
        questionAnswers = null,
        roomId = 220
)

fun Application.launchSyncJob() {
    val config = environment.config.config("sessionize")
    val url = config.property("url").getString()
    val interval = config.property("interval").getString().toLong()

    log.info("Synchronizing each $interval minutes with $url")
    launch(CommonPool) {
        while (true) {
            log.trace("Synchronizing to Sessionize…")
            val client = HttpClient(CIO)
            val response = client.call(URL(url)) {}
            val text = response.receive<String>()
            var data = gson.fromJson<AllData>(text)
            data = data.copy(sessions = data.sessions?.plus(fakeVotingSession))
            log.trace("Finished loading data from Sessionize.")
            sessionizeData = SessionizeData(data)
            delay(interval, TimeUnit.MINUTES)
        }
    }
}
