import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinconf.ConferenceInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MapsApiTest {
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
    fun `conference-info with year returns map data for that year`() = runTest {
        val response = client.get("/2025/conference-info")
        assertEquals(HttpStatusCode.OK, response.status)

        val mapData = response.body<ConferenceInfo>().mapData
        assertNotNull(mapData)
        assertEquals(2, mapData.floors.size)
        assertTrue(mapData.floors[0].svgPathLight.contains("maps/ground-floor.svg"))
    }

    @Test
    fun `conference-info for archived year returns that year's map data`() = runTest {
        val response = client.get("/2024/conference-info")
        assertEquals(HttpStatusCode.OK, response.status)

        val info = response.body<ConferenceInfo>()
        val mapData = info.mapData
        assertNotNull(mapData)

        assertEquals(1, mapData.floors.size)
        assertEquals("Main hall", mapData.floors[0].name)
        assertTrue(mapData.floors[0].svgPathLight.contains("maps/main-hall.svg"))
        assertTrue(mapData.floors[0].svgPathDark.contains("maps/main-hall-dark.svg"))

        assertEquals(1, mapData.rooms.size)
        assertTrue(mapData.rooms.containsKey("Room 1"))
    }

    @Test
    fun `map SVG endpoint with year returns SVG content`() = runTest {
        val response = client.get("/2025/maps/ground-floor.svg")
        assertEquals(HttpStatusCode.OK, response.status)

        val content = response.bodyAsText()
        assertTrue(content.contains("Ground floor 2025"))
    }

    @Test
    fun `map SVG endpoint for archived year returns that year's SVG`() = runTest {
        val response = client.get("/2024/maps/main-hall.svg")
        assertEquals(HttpStatusCode.OK, response.status)

        val content = response.bodyAsText()
        assertTrue(content.contains("Main hall 2024"))
    }

    @Test
    fun `map SVG endpoint for dark variant returns dark SVG`() = runTest {
        val response = client.get("/2025/maps/ground-floor-dark.svg")
        assertEquals(HttpStatusCode.OK, response.status)

        val content = response.bodyAsText()
        assertTrue(content.contains("Ground floor dark 2025"))
    }

    @Test
    fun `map SVG endpoint with nonexistent filename returns 404`() = runTest {
        val response = client.get("/2025/maps/nonexistent.svg")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `map SVG endpoint with unsupported year returns 404`() = runTest {
        val response = client.get("/2020/maps/ground-floor.svg")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `map SVG URLs in conference-info are valid and fetchable`() = runTest {
        val info = client.get("/2025/conference-info").body<ConferenceInfo>()
        val mapData = info.mapData

        for (floor in mapData.floors) {
            val lightResponse = client.get(floor.svgPathLight)
            assertEquals(HttpStatusCode.OK, lightResponse.status, "Failed to fetch ${floor.svgPathLight}")

            val darkResponse = client.get(floor.svgPathDark)
            assertEquals(HttpStatusCode.OK, darkResponse.status, "Failed to fetch ${floor.svgPathDark}")
        }
    }
}
