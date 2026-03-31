import io.ktor.server.config.*
import org.jetbrains.kotlinconf.backend.services.ArchivedDataService
import org.jetbrains.kotlinconf.backend.services.AssetService
import org.jetbrains.kotlinconf.backend.services.ConferenceInfoService
import org.jetbrains.kotlinconf.backend.services.GoldenKodeeService
import org.jetbrains.kotlinconf.backend.utils.ConferenceConfig
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Runs the same data validations that run on server startup,
 * against the production resource files, and fails on any issues.
 */
class DataValidationTest {
    private val config = ConferenceConfig(ApplicationConfig("application.yaml"))

    @Test
    fun validateArchives() {
        val issues = ArchivedDataService(config).validateArchives()
        assertEquals(0, issues, "Archived conference data has validation issues")
    }

    @Test
    fun validateConferenceInfo() {
        val issues = ConferenceInfoService(config).validateConferenceInfo()
        assertEquals(0, issues, "Conference info data has validation issues")
    }

    @Test
    fun validateGoldenKodeeData() {
        val issues = GoldenKodeeService(config).validateGoldenKodeeData()
        assertEquals(0, issues, "Golden Kodee data has validation issues")
    }

    @Test
    fun validateDocuments() {
        val issues = AssetService(config).validateDocuments()
        assertEquals(0, issues, "Document data has validation issues")
    }
}
