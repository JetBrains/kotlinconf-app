import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.Score
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The admin endpoints /vote/all and /feedback/summary expose the voting user's
 * identifier so the admin dashboard can join a feedback comment with the score
 * the same user gave the session. These tests pin that contract.
 */
class AdminVotesApiTest {
    private val adminToken = "test-secret"

    private val app = TestApplication {
        environment {
            config = ApplicationConfig("test-application.yaml")
        }
    }

    private val client = app.createClient {
        install(HttpRequestRetry)
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `vote_all without authorization returns 401`() = runTest {
        val response = client.get("/2025/vote/all")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `vote_all with non-admin token returns 401`() = runTest {
        val response = client.get("/2025/vote/all") {
            header(HttpHeaders.Authorization, "Bearer not-the-admin-token")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `feedback_summary without authorization returns 401`() = runTest {
        val response = client.get("/2025/feedback/summary")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `feedback_summary with non-admin token returns 401`() = runTest {
        val response = client.get("/2025/feedback/summary") {
            header(HttpHeaders.Authorization, "Bearer not-the-admin-token")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `vote_all admin response carries userId per vote`() = runTest {
        val response = client.get("/2025/vote/all") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // If the response items don't have a userId, kotlinx-serialization will
        // fail to deserialize into AdminVoteRow and the test fails loudly.
        val votes = response.body<List<AdminVoteRow>>()
        for (v in votes) check(v.userId.isNotEmpty()) { "userId must be non-empty" }
    }

    @Test
    fun `feedback_summary admin response carries userId per feedback row`() = runTest {
        val response = client.get("/2025/feedback/summary") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val feedback = response.body<List<AdminFeedbackRow>>()
        for (f in feedback) check(f.userId.isNotEmpty()) { "userId must be non-empty" }
    }

    // Local mirrors of the admin response shape; userId is a required (non-null)
    // field on purpose — so the test breaks if the server drops it.
    @Serializable
    data class AdminVoteRow(val userId: String, val sessionId: SessionId, val score: Score?)

    @Serializable
    data class AdminFeedbackRow(val userId: String, val sessionId: SessionId, val value: String)
}
