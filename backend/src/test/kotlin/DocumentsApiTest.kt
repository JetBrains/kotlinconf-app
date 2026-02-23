import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DocumentsApiTest {
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
    fun `documents bulk endpoint without year returns all documents for current year`() = runTest {
        val response = client.get("/documents")
        assertEquals(HttpStatusCode.OK, response.status)

        val docs = response.body<Map<String, String>>()
        assertEquals(3, docs.size)

        listOf("code-of-conduct", "visitors-privacy-notice", "visitors-terms")
            .forEach { docName ->
                assertTrue(docs.containsKey(docName))
                assertTrue(docs.getValue(docName).contains("2025"))
            }
    }

    @Test
    fun `documents bulk endpoint with year returns documents for that year`() = runTest {
        val response = client.get("/2024/documents")
        assertEquals(HttpStatusCode.OK, response.status)

        val docs = response.body<Map<String, String>>()
        assertEquals(3, docs.size)
        assertTrue(docs.getValue("code-of-conduct").contains("2024"))
    }

    @Test
    fun `documents bulk endpoint with current year returns current year documents`() = runTest {
        val response = client.get("/2025/documents")
        assertEquals(HttpStatusCode.OK, response.status)

        val docs = response.body<Map<String, String>>()
        assertEquals(3, docs.size)
        assertTrue(docs.getValue("code-of-conduct").contains("2025"))
    }

    @Test
    fun `documents bulk endpoint returns different content per year`() = runTest {
        val docs2024 = client.get("/2024/documents").body<Map<String, String>>()
        val docs2025 = client.get("/2025/documents").body<Map<String, String>>()

        assertTrue(docs2024.getValue("code-of-conduct").contains("2024"))
        assertTrue(docs2025.getValue("code-of-conduct").contains("2025"))
    }

    @Test
    fun `documents bulk endpoint with unsupported year returns 404`() = runTest {
        val response = client.get("/2020/documents")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `single document endpoint returns document content`() = runTest {
        val response = client.get("/2025/documents/code-of-conduct")
        assertEquals(HttpStatusCode.OK, response.status)

        val content = response.bodyAsText()
        assertTrue(content.contains("Code of Conduct"))
        assertTrue(content.contains("2025"))
    }

    @Test
    fun `single document endpoint without year returns document for current year`() = runTest {
        val response = client.get("/documents/code-of-conduct")
        assertEquals(HttpStatusCode.OK, response.status)

        val content = response.bodyAsText()
        assertTrue(content.contains("Code of Conduct"))
        assertTrue(content.contains("2025"))
    }

    @Test
    fun `single document endpoint with archived year returns archived document`() = runTest {
        val response = client.get("/2024/documents/code-of-conduct")
        assertEquals(HttpStatusCode.OK, response.status)

        val content = response.bodyAsText()
        assertTrue(content.contains("2024"))
    }

    @Test
    fun `single document endpoint with nonexistent name returns 404`() = runTest {
        val response = client.get("/2025/documents/nonexistent")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `single document endpoint with unsupported year returns 404`() = runTest {
        val response = client.get("/2020/documents/code-of-conduct")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `single document endpoint with invalid year returns 404`() = runTest {
        val response = client.get("/invalid/documents/code-of-conduct")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
