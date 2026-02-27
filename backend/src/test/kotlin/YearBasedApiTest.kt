import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.FeedbackInfo
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.VoteInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class YearBasedApiTest {
    val app = TestApplication {
        environment {
            config = ApplicationConfig("test-application.yaml")
        }
    }

    val client = app.createClient {
        install(HttpRequestRetry)
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `conference endpoint without year returns current year data`() = runTest {
        val response = client.get("/conference")
        assertEquals(HttpStatusCode.OK, response.status)

        // Verify that we have some sessions
        assertNotEquals(0, response.body<Conference>().sessions.size)
    }

    @Test
    fun `conference endpoint with current year returns data`() = runTest {
        val response = client.get("/2025/conference")
        assertEquals(HttpStatusCode.OK, response.status)

        // Verify that we have some sessions
        assertNotEquals(0, response.body<Conference>().sessions.size)
    }

    @Test
    fun `conference endpoint with archived year returns archived data`() = runTest {
        val response = client.get("/2024/conference")
        assertEquals(HttpStatusCode.OK, response.status)
        val conference = response.body<Conference>()

        // Verify this is archived data by checking for our test session
        assertTrue(conference.sessions.any { it.id.id == "test-session-2024" })
    }

    @Test
    fun `conference endpoint with unsupported year returns 404`() = runTest {
        val response = client.get("/2020/conference")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `conference endpoint with invalid year returns 404`() = runTest {
        val response = client.get("/invalid/conference")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `vote POST on archived year returns 403`() = runTest {
        // First sign up a user
        val userId = "test-user-${System.currentTimeMillis()}"
        client.post("/sign") {
            setBody(userId)
            contentType(ContentType.Text.Plain)
        }

        // Try to vote on archived year
        val response = client.post("/2024/vote") {
            header(HttpHeaders.Authorization, "Bearer $userId")
            contentType(ContentType.Application.Json)
            setBody(VoteInfo(SessionId("test-session-2024"), Score.GOOD))
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `feedback POST on archived year returns 403`() = runTest {
        // First sign up a user
        val userId = "test-user-feedback-${System.currentTimeMillis()}"
        client.post("/sign") {
            setBody(userId)
            contentType(ContentType.Text.Plain)
        }

        // Try to submit feedback on archived year
        val response = client.post("/2024/feedback") {
            header(HttpHeaders.Authorization, "Bearer $userId")
            contentType(ContentType.Application.Json)
            setBody(FeedbackInfo(SessionId("test-session-2024"), "Great!"))
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
