import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinconf.Conference
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

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

    @Test
    fun testWorkshopPartsAreMerged() = runTest {
        val conference = client.get("/conference").body<Conference>()
        val workshops = conference.sessions.filter { it.tags?.contains("Workshop") == true }

        for (workshop in workshops) {
            assertFalse("Part" in workshop.title)
        }
    }
}
