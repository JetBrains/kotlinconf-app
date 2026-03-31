// ABOUTME: Tests for the SQL migration runner mechanics (discovery, application, idempotency).
// ABOUTME: Uses in-memory H2 with PostgreSQL compatibility mode.

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.kotlinconf.backend.schema.MigrationRunner
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MigrationRunnerTest {
    private lateinit var database: Database

    @BeforeTest
    fun setup() {
        database = Database.connect(
            "jdbc:h2:mem:test_runner_${System.nanoTime()};MODE=PostgreSQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
            "org.h2.Driver"
        )
    }

    @Test
    fun `discoverMigrations finds and sorts SQL files`() {
        val migrations = MigrationRunner.discoverMigrations()
        assertTrue(migrations.isNotEmpty(), "Should discover at least one migration")
        assertEquals(1, migrations.first().version, "First migration should be V001")
        assertEquals(
            migrations.sortedBy { it.version },
            migrations,
            "Migrations should be sorted by version"
        )
    }

    @Test
    fun `migrate applies pending migrations and records them`() {
        MigrationRunner.migrate(database)
        val applied = MigrationRunner.appliedVersions(database)
        assertTrue(applied.isNotEmpty(), "Should have applied at least one migration")
        assertTrue(1 in applied, "V001 should be applied")
        assertTrue(2 in applied, "V002 should be applied")
    }

    @Test
    fun `migrate is idempotent`() {
        MigrationRunner.migrate(database)
        val versionAfterFirst = MigrationRunner.currentVersion(database)

        MigrationRunner.migrate(database)
        val versionAfterSecond = MigrationRunner.currentVersion(database)

        assertEquals(versionAfterFirst, versionAfterSecond, "Second migrate should be a no-op")
    }

    @Test
    fun `currentVersion reflects applied state`() {
        assertEquals(0, MigrationRunner.currentVersion(database), "Should be 0 before any migrations")
        MigrationRunner.migrate(database)
        assertTrue(MigrationRunner.currentVersion(database) > 0, "Should be > 0 after migrations")
    }
}
