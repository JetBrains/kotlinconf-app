import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.Conference
import org.jetbrains.kotlinconf.NewsItem
import org.jetbrains.kotlinconf.NewsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

private const val ADMIN_SECRET = "test-secret"

class ApiTest {

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
    fun testConferenceCallRepeatable() = runTest {
        repeat(10) {
            client.get("/conference").body<Conference>()
        }
    }


    private suspend fun createTestNews(): String {
        val newsRequest = NewsRequest(
            title = "Test News",
            publicationDate = LocalDateTime.parse("2024-01-20T00:00:00"),
            content = "Test news content",
            photoUrl = "https://example.com/image.jpg"
        )
        val response = client.post("/news") {
            headers.append(HttpHeaders.Authorization, "Bearer $ADMIN_SECRET")
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(newsRequest)
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val result = response.body<Map<String, String>>()
        assertNotNull(result["id"])
        return result["id"]!!
    }

    @Test
    fun testWorkshopPartsAreMerged() = runTest {
        val conference = client.get("/conference").body<Conference>()
        val workshops = conference.sessions.filter { it.tags?.contains("Workshop") == true }

        for (workshop in workshops) {
            assertFalse("Part" in workshop.title)
        }
    }

    @Test
    fun testGetNews() = runTest {
        val response = client.get("/news")
        assertEquals(HttpStatusCode.OK, response.status)
        val news = response.body<List<NewsItem>>()
        assertNotNull(news)
    }

    @Test
    fun testCreateNewsUnauthorized() = runTest {
        val newsRequest = NewsRequest(
            title = "Test News",
            publicationDate = LocalDateTime.parse("2024-01-20T00:00:00"),
            content = "Test news content",
            photoUrl = "https://example.com/image.jpg"
        )
        val response = client.post("/news") {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(newsRequest)
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testCreateNewsAuthorized() = runTest {
        val id = createTestNews()
        assertNotNull(id)
    }

    @Test
    fun testUpdateNewsUnauthorized() = runTest {
        val id = createTestNews()
        val newsRequest = NewsRequest(
            title = "Updated Test News",
            publicationDate = LocalDateTime.parse("2024-01-21T00:00:00"),
            content = "Updated test news content",
            photoUrl = "https://example.com/image.jpg"
        )
        val response = client.put("/news/$id") {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(newsRequest)
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testUpdateNewsAuthorized() = runTest {
        val id = createTestNews()
        val newsRequest = NewsRequest(
            title = "Updated Test News",
            publicationDate = LocalDateTime.parse("2024-01-21T00:00:00"),
            content = "Updated test news content",
            photoUrl = "https://example.com/image.jpg"
        )
        val response = client.put("/news/$id") {
            headers.append(HttpHeaders.Authorization, "Bearer $ADMIN_SECRET")
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(newsRequest)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val updatedNews = client.get("/news").body<List<NewsItem>>()
        val updated = updatedNews.find { it.id == id }
        assertNotNull(updated)
        assertEquals("Updated Test News", updated!!.title)
    }

    @Test
    fun testDeleteNewsUnauthorized() = runTest {
        val id = createTestNews()
        val response = client.delete("/news/$id")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun testDeleteNewsAuthorized() = runTest {
        val id = createTestNews()
        val response = client.delete("/news/$id") {
            headers.append(HttpHeaders.Authorization, "Bearer $ADMIN_SECRET")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val news = client.get("/news").body<List<NewsItem>>()
        assertFalse(news.any { it.id == id })
    }
}
