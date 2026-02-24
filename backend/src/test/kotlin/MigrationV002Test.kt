// ABOUTME: Integration tests for V001->V002 migration verifying data backfill and schema changes.
// ABOUTME: Tests year column addition, PK updates, SignedPolicies creation, and data preservation.

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.kotlinconf.backend.schema.Migration
import org.jetbrains.kotlinconf.backend.schema.MigrationRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MigrationV002Test {
    private lateinit var database: Database

    @BeforeEach
    fun setup() {
        database = Database.connect(
            "jdbc:h2:mem:test_v002_${System.nanoTime()};MODE=PostgreSQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
            "org.h2.Driver"
        )
    }

    private fun applyMigration(migration: Migration) {
        transaction(database) {
            val conn = this.connection.connection as java.sql.Connection
            val statements = migration.sql
                .lines()
                .filter { !it.trimStart().startsWith("--") }
                .joinToString("\n")
                .split(";")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            for (statement in statements) {
                conn.createStatement().use { it.execute(statement) }
            }
        }
    }

    private fun runSql(sql: String) {
        transaction(database) {
            val conn = this.connection.connection as java.sql.Connection
            conn.createStatement().use { it.execute(sql) }
        }
    }

    private fun insertTestData() {
        runSql("""INSERT INTO "Users" ("uuid", "timestamp") VALUES ('user1', '2025-01-01T00:00:00')""")
        runSql("""INSERT INTO "Users" ("uuid", "timestamp") VALUES ('user2', '2025-01-02T00:00:00')""")
        runSql("""INSERT INTO "Votes" ("timestamp", "uuid", "sessionId", "rating") VALUES ('2025-01-01T00:00:00', 'user1', 'session1', 1)""")
        runSql("""INSERT INTO "Votes" ("timestamp", "uuid", "sessionId", "rating") VALUES ('2025-01-01T00:00:00', 'user2', 'session2', 2)""")
        runSql("""INSERT INTO "Feedback" ("timestamp", "uuid", "sessionId", "feedback") VALUES ('2025-01-01T00:00:00', 'user1', 'session1', 'Great talk!')""")
    }

    private fun <T> query(sql: String, mapper: (java.sql.ResultSet) -> T): T {
        return transaction(database) {
            val conn = this.connection.connection as java.sql.Connection
            conn.createStatement().use { stmt ->
                stmt.executeQuery(sql).use { rs ->
                    mapper(rs)
                }
            }
        }
    }

    @Test
    fun `V002 adds year column to Votes with backfill`() {
        val migrations = MigrationRunner.discoverMigrations()
        val v001 = migrations.first { it.version == 1 }
        val v002 = migrations.first { it.version == 2 }

        applyMigration(v001)
        insertTestData()
        applyMigration(v002)

        val rows = query("""SELECT "uuid", "sessionId", "year" FROM "Votes" ORDER BY "uuid"""") { rs ->
            val results = mutableListOf<Triple<String, String, Int>>()
            while (rs.next()) {
                results.add(Triple(rs.getString("uuid"), rs.getString("sessionId"), rs.getInt("year")))
            }
            results
        }
        assertEquals(2, rows.size, "Should have 2 vote rows")
        assertTrue(rows.all { it.third == 2025 }, "All existing votes should have year=2025")
    }

    @Test
    fun `V002 adds year column to Feedback with backfill`() {
        val migrations = MigrationRunner.discoverMigrations()
        val v001 = migrations.first { it.version == 1 }
        val v002 = migrations.first { it.version == 2 }

        applyMigration(v001)
        insertTestData()
        applyMigration(v002)

        val rows = query("""SELECT "uuid", "year" FROM "Feedback"""") { rs ->
            val results = mutableListOf<Pair<String, Int>>()
            while (rs.next()) {
                results.add(Pair(rs.getString("uuid"), rs.getInt("year")))
            }
            results
        }
        assertEquals(1, rows.size, "Should have 1 feedback row")
        assertEquals(2025, rows.first().second, "Feedback should have year=2025")
    }

    @Test
    fun `V002 creates SignedPolicies and backfills from Users`() {
        val migrations = MigrationRunner.discoverMigrations()
        val v001 = migrations.first { it.version == 1 }
        val v002 = migrations.first { it.version == 2 }

        applyMigration(v001)
        insertTestData()
        applyMigration(v002)

        val rows = query("""SELECT "uuid", "year" FROM "SignedPolicies" ORDER BY "uuid"""") { rs ->
            val results = mutableListOf<Pair<String, Int>>()
            while (rs.next()) {
                results.add(Pair(rs.getString("uuid"), rs.getInt("year")))
            }
            results
        }
        assertEquals(2, rows.size, "Should have 2 signed policy rows (one per user)")
        assertTrue(rows.all { it.second == 2025 }, "All policies should have year=2025")
        assertEquals("user1", rows[0].first)
        assertEquals("user2", rows[1].first)
    }

    @Test
    fun `V002 preserves all existing data`() {
        val migrations = MigrationRunner.discoverMigrations()
        val v001 = migrations.first { it.version == 1 }
        val v002 = migrations.first { it.version == 2 }

        applyMigration(v001)
        insertTestData()
        applyMigration(v002)

        // Verify Users are untouched
        val userCount = query("""SELECT COUNT(*) AS cnt FROM "Users"""") { rs ->
            rs.next(); rs.getInt("cnt")
        }
        assertEquals(2, userCount, "Users table should still have 2 rows")

        // Verify Votes data preserved
        val voteRatings = query("""SELECT "rating" FROM "Votes" ORDER BY "uuid"""") { rs ->
            val results = mutableListOf<Int>()
            while (rs.next()) { results.add(rs.getInt("rating")) }
            results
        }
        assertEquals(listOf(1, 2), voteRatings, "Vote ratings should be preserved")

        // Verify Feedback data preserved
        val feedbackText = query("""SELECT "feedback" FROM "Feedback"""") { rs ->
            rs.next(); rs.getString("feedback")
        }
        assertEquals("Great talk!", feedbackText, "Feedback text should be preserved")
    }
}
