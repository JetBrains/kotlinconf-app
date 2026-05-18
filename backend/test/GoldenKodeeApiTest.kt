import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinconf.GoldenKodeeData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GoldenKodeeApiTest {
    val app = TestApplication {
        environment {
            config = ApplicationConfig("test-application.yaml")
        }
    }

    val client = app.createClient {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `golden-kodee endpoint with current year returns data`() = runTest {
        val response = client.get("/2025/golden-kodee")
        assertEquals(HttpStatusCode.OK, response.status)

        val data = response.body<GoldenKodeeData>()
        assertNotEquals(0, data.categories.size)
    }

    @Test
    fun `golden-kodee endpoint with unsupported year returns 404`() = runTest {
        val response = client.get("/2020/golden-kodee")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `golden-kodee endpoint with invalid year returns 404`() = runTest {
        val response = client.get("/invalid/golden-kodee")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `golden-kodee endpoint for year without data returns 404`() = runTest {
        val response = client.get("/2024/golden-kodee")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `golden-kodee response contains expected categories`() = runTest {
        val data = client.get("/2025/golden-kodee").body<GoldenKodeeData>()

        assertEquals(4, data.categories.size)

        val categoryIds = data.categories.map { it.id.id }
        assertTrue("creativity" in categoryIds)
        assertTrue("community-impact" in categoryIds)
        assertTrue("best-library" in categoryIds)
        assertTrue("rising-star" in categoryIds)
    }

    @Test
    fun `golden-kodee categories have nominees`() = runTest {
        val data = client.get("/2025/golden-kodee").body<GoldenKodeeData>()

        for (category in data.categories) {
            assertNotEquals(0, category.nominees.size, "Category '${category.title}' has no nominees")
        }
    }

    @Test
    fun `golden-kodee nominees have required fields`() = runTest {
        val data = client.get("/2025/golden-kodee").body<GoldenKodeeData>()

        for (category in data.categories) {
            for (nominee in category.nominees) {
                assertTrue(nominee.name.isNotBlank(), "Nominee ${nominee.id} has blank name")
                assertTrue(nominee.photoUrl.isNotBlank(), "Nominee ${nominee.id} has blank photoUrl")
            }
        }
    }

    @Test
    fun `golden-kodee each category has one winner at most`() = runTest {
        val data = client.get("/2025/golden-kodee").body<GoldenKodeeData>()

        for (category in data.categories) {
            val winners = category.nominees.count { it.winner }
            assertTrue(winners <= 1, "Category '${category.title}' should have at most one winner, but has $winners")
        }
    }
}
