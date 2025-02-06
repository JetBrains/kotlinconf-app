package org.jetbrains.kotlinconf

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.date.GMTDate
import io.ktor.util.date.Month
import io.ktor.utils.io.core.Closeable
import org.jetbrains.kotlinconf.utils.appLogger

val HTTP_CLIENT = HttpClient()

/**
 * Adapter to handle backend API and manage auth information.
 */
class APIClient(
    private val apiUrl: String
) : Closeable {
    var userId: String? = null

    private val client = HTTP_CLIENT.config {
        install(ContentNegotiation) {
            json()
        }

        install(Logging) {
            level = LogLevel.HEADERS
            logger = appLogger()
        }

        HttpResponseValidator {
            validateResponse {
                when (it.status) {
                    COMEBACK_LATER_STATUS -> throw TooEarlyVote()
                    TOO_LATE_STATUS -> throw TooLateVote()
                    HttpStatusCode.Conflict -> return@validateResponse
                    HttpStatusCode.Unauthorized -> throw Unauthorized()
                }
            }
        }

        install(HttpRequestRetry) {
            maxRetries = Int.MAX_VALUE
            delay {
                kotlinx.coroutines.delay(it)
            }
            constantDelay(10 * 1000L)
            retryOnException(retryOnTimeout = true)
        }

        install(DefaultRequest) {
            url.takeFrom(apiUrl)
        }
    }

    /**
     * @return status of request.
     */
    suspend fun sign(): Boolean {
        val userId = userId ?: return false

        val response = client.post {
            apiUrl("sign")
            setBody(userId)
        }

        return response.status.isSuccess()
    }

    /**
     * Get [ConferenceData] info
     */
    suspend fun downloadConferenceData(): Conference = client.get {
        url.path("conference")
    }.body()

    /**
     * Vote for session.
     */
    suspend fun vote(sessionId: SessionId, score: Score?): Boolean {
        if (userId == null) return false

        client.post {
            apiUrl("vote")
            json()
            setBody(VoteInfo(sessionId, score))
        }

        return true
    }

    /**
     * Send feedback
     */
    suspend fun sendFeedback(sessionId: SessionId, feedback: String): Boolean {
        if (userId == null) return false

        client.post {
            apiUrl("feedback")
            json()
            setBody(FeedbackInfo(sessionId, feedback))
        }

        return true
    }

    /**
     * List my votes.
     */
    suspend fun myVotes(): List<VoteInfo> {
        if (userId == null) return emptyList()

        return client.get {
            apiUrl("vote")
        }.body<Votes>().votes
    }

    /**
     * Get server time.
     */
    suspend fun getServerTime(): GMTDate = client.get {
        apiUrl("time")
    }.bodyAsText().let { response -> GMTDate(response.toLong()) }

    // TODO real api call
    suspend fun getNews(): List<NewsItem> = EXAMPLE_NEWS_ITEMS

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        if (userId != null) {
            header(HttpHeaders.Authorization, "Bearer $userId")
        }

        header(HttpHeaders.CacheControl, "no-cache")

        url {
            encodedPath = path
        }
    }

    override fun close() {
        client.close()
    }
}

private val EXAMPLE_NEWS_ITEMS = listOf(
    NewsItem(
        id = "0",
        title = "Kotlin 1.9 Released",
        content = "**Exciting news for Kotlin developers!** The latest version of Kotlin brings significant improvements and new features to enhance your development experience.\n\nSome highlights include:\n- *K2 compiler* improvements for faster compilation\n- Enhanced type inference system\n- New stdlib functions\n\nCheck out the detailed release notes at [kotlinlang.org](https://kotlinlang.org) and start exploring these amazing features today! The Kotlin team has been working hard to make this release even more **powerful** and *developer-friendly*.",
        date = GMTDate(
            year = 2024,
            month = Month.APRIL,
            dayOfMonth = 23,
            hours = 10,
            minutes = 24,
            seconds = 2
        ),
        photoUrl = "https://picsum.photos/1800/900"
    ),
    NewsItem(
        id = "1",
        title = "KotlinConf 2024 Announced",
        content = "Get ready for the most anticipated Kotlin event of the year! **KotlinConf 2024** brings together developers from around the world for an unforgettable experience.\n\n*What to expect:*\n- Inspiring keynotes from Kotlin leaders\n- In-depth technical sessions\n- Hands-on workshops\n- Networking opportunities\n\nDon't miss the chance to meet fellow Kotlin enthusiasts and learn from industry experts. Early bird tickets are now available at [kotlinconf.com/2024](https://kotlinconf.com/2024).\n\n**Pro tip:** Check out the *conference app* to plan your schedule and connect with other attendees!",
        date = GMTDate(year = 2024, month = Month.MAY, dayOfMonth = 22, hours = 10, minutes = 24, seconds = 2),
        photoUrl = null
    ),
    NewsItem(
        id = "2",
        title = "Jetpack Compose Updates",
        content = "The world of **Jetpack Compose** continues to evolve with exciting new features for both Android and Desktop development!\n\n*Latest improvements include:*\n- Enhanced performance optimizations\n- New material design components\n- Improved animation APIs\n- Better desktop window management\n\nRead the comprehensive guide on the [Android Developers Blog](https://android-developers.googleblog.com) and explore the [Compose Multiplatform documentation](https://www.jetbrains.com/compose-multiplatform/).\n\n**Did you know?** You can now easily share up to *90% of your UI code* between Android and Desktop applications using Compose Multiplatform!",
        date = GMTDate(
            year = 2024,
            month = Month.JANUARY,
            dayOfMonth = 22,
            hours = 10,
            minutes = 24,
            seconds = 2
        ),
        photoUrl = null
    ),
    NewsItem(
        id = "3",
        title = "New Kotlin Multiplatform Features",
        content = "**Kotlin Multiplatform** technology reaches new heights with groundbreaking features and improvements!\n\n*Key highlights of the latest release:*\n- Simplified project setup and configuration\n- Enhanced iOS integration with new Kotlin/Native features\n- Improved dependency management\n- Extended WebAssembly support\n\nStart building your next cross-platform project with [KMP](https://kotlinlang.org/docs/multiplatform.html) today!\n\n**Success Story:** *Philips* recently shared how they achieved a **75% code sharing rate** across platforms using Kotlin Multiplatform. Read their detailed case study on the [Kotlin Blog](https://blog.jetbrains.com/kotlin/).\n\nExplore the [official documentation](https://kotlinlang.org/docs/multiplatform-get-started.html) to learn more about these exciting developments!",
        date = GMTDate(year = 2024, month = Month.MAY, dayOfMonth = 23, hours = 10, minutes = 24, seconds = 2),
        photoUrl = "https://picsum.photos/1800/900"
    ),
    NewsItem(
        id = "4",
        title = "Kotlin Community Highlights",
        content = "The **Kotlin community** continues to innovate and inspire! Let's celebrate some remarkable community contributions.\n\n*Featured Projects:*\n- **Ktor 2.0**: A powerful framework for building asynchronous servers and clients\n- *Kotlin Native Bridge*: Seamless integration between Kotlin and native platforms\n- **KMP-NativeCoroutines**: Simplified concurrency for multiplatform projects\n\nJoin the community on [Kotlin Slack](https://kotlinlang.slack.com) with over *100,000 members* and share your own projects!\n\n**Want to contribute?** Check out the [Kotlin Contributing Guidelines](https://github.com/JetBrains/kotlin) and help shape the future of Kotlin. The community has already contributed more than *500 patches* to the latest release!\n\nExplore more community projects on [Kotlin Weekly](https://kotlinweekly.net) and get inspired for your next project.",
        date = GMTDate(
            year = 2024,
            month = Month.APRIL,
            dayOfMonth = 20,
            hours = 10,
            minutes = 24,
            seconds = 2
        ),
        photoUrl = "https://picsum.photos/1800/900"
    )
)
