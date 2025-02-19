import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.TestApplication
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
        install(HttpRequestRetry) {
        }
        install(ContentNegotiation) {
            json()
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