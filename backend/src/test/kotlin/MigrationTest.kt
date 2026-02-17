import org.h2.jdbc.JdbcSQLSyntaxErrorException
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.kotlinconf.backend.schema.initializeDatabase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MigrationTest {

    /**
     * Tests database schema migrations:
     *  - Adding year column to Votes, initialized to 2025 for existing data
     *  - Adding year column to Feedback, initialized to 2025 for existing data
     *  - Create SignedPolicies table, with existing users added with a 2025 signed policy
     */
    @Test
    fun from2025to2026() {
        Database.connect(
            "jdbc:h2:mem:migration_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
            driver = "org.h2.Driver"
        )

        // Create the old schema (no year columns, no SignedPolicies table)
        transaction {
            exec("""
                CREATE TABLE IF NOT EXISTS "Users" (
                    "uuid" VARCHAR(50) NOT NULL,
                    "timestamp" VARCHAR(50) NOT NULL,
                    CONSTRAINT "pk_Users" PRIMARY KEY ("uuid")
                )
            """.trimIndent())

            exec("""
                CREATE TABLE IF NOT EXISTS "Votes" (
                    "timestamp" VARCHAR(50) NOT NULL,
                    "uuid" VARCHAR(50) NOT NULL,
                    "sessionId" VARCHAR(50) NOT NULL,
                    "rating" INT NOT NULL,
                    CONSTRAINT "pk_Votes" PRIMARY KEY ("uuid", "sessionId")
                )
            """.trimIndent())

            exec("""
                CREATE TABLE IF NOT EXISTS "Feedback" (
                    "timestamp" VARCHAR(50) NOT NULL,
                    "uuid" VARCHAR(50) NOT NULL,
                    "sessionId" VARCHAR(50) NOT NULL,
                    "feedback" VARCHAR(5000) NOT NULL,
                    CONSTRAINT "pk_Feedback" PRIMARY KEY ("uuid", "sessionId")
                )
            """.trimIndent())

            // Insert sample data
            exec("""
                INSERT INTO "Users" ("uuid", "timestamp") VALUES
                ('user-1', '2025-05-22T09:00:00'),
                ('user-2', '2025-05-22T09:30:00'),
                ('user-3', '2025-05-22T09:45:00')
            """.trimIndent())

            exec("""
                INSERT INTO "Votes" ("timestamp", "uuid", "sessionId", "rating") VALUES
                ('2025-05-22T10:00:00', 'user-1', 'session-101', 1),
                ('2025-05-22T10:05:00', 'user-1', 'session-102', -1),
                ('2025-05-22T10:10:00', 'user-2', 'session-101', 1)
            """.trimIndent())

            exec("""
                INSERT INTO "Feedback" ("timestamp", "uuid", "sessionId", "feedback") VALUES
                ('2025-05-22T11:00:00', 'user-1', 'session-101', 'Great talk!'),
                ('2025-05-22T11:05:00', 'user-2', 'session-102', 'Very informative')
            """.trimIndent())
        }

        val before = transaction {
            dumpAllTables("BEFORE MIGRATION")
        }

        assertEquals(
            """
            ============ BEFORE MIGRATION ============
            --- Users ---
            uuid | timestamp
            ----------------
            user-1 | 2025-05-22T09:00:00
            user-2 | 2025-05-22T09:30:00
            user-3 | 2025-05-22T09:45:00

            --- Votes ---
            timestamp | uuid | sessionId | rating
            -------------------------------------
            2025-05-22T10:00:00 | user-1 | session-101 | 1
            2025-05-22T10:05:00 | user-1 | session-102 | -1
            2025-05-22T10:10:00 | user-2 | session-101 | 1

            --- Feedback ---
            timestamp | uuid | sessionId | feedback
            ---------------------------------------
            2025-05-22T11:00:00 | user-1 | session-101 | Great talk!
            2025-05-22T11:05:00 | user-2 | session-102 | Very informative

            --- SignedPolicies ---
            [does not exist]
            ==========================================
            """.trimIndent(),
            before
        )

        initializeDatabase()

        val after = transaction {
            dumpAllTables("AFTER MIGRATION")
        }

        assertEquals(
            """
            ============ AFTER MIGRATION ============
            --- Users ---
            uuid | timestamp
            ----------------
            user-1 | 2025-05-22T09:00:00
            user-2 | 2025-05-22T09:30:00
            user-3 | 2025-05-22T09:45:00

            --- Votes ---
            timestamp | uuid | sessionId | rating
            -------------------------------------
            2025-05-22T10:00:00 | user-1 | session-101 | 1
            2025-05-22T10:05:00 | user-1 | session-102 | -1
            2025-05-22T10:10:00 | user-2 | session-101 | 1

            --- Feedback ---
            timestamp | uuid | sessionId | feedback
            ---------------------------------------
            2025-05-22T11:00:00 | user-1 | session-101 | Great talk!
            2025-05-22T11:05:00 | user-2 | session-102 | Very informative

            --- SignedPolicies ---
            UUID | year
            -----------
            user-1 | 2025
            user-2 | 2025
            user-3 | 2025

            =========================================
            """.trimIndent(),
            after
        )
    }

    private fun dumpAllTables(label: String): String = buildString {
        val header = "============ $label ============"
        appendLine(header)
        appendTable("Users")
        appendTable("Votes")
        appendTable("Feedback")
        appendTable("SignedPolicies")
        append("=".repeat(header.length))
    }

    private fun StringBuilder.appendTable(tableName: String) {
        appendLine("--- $tableName ---")
        val conn = TransactionManager.current().connection.connection as java.sql.Connection
        val rs = try {
            conn.createStatement().executeQuery("""SELECT * FROM "$tableName"""")
        } catch (_: JdbcSQLSyntaxErrorException) {
            appendLine("[does not exist]")
            return
        }
        val meta = rs.metaData
        val colCount = meta.columnCount
        val colNames = (1..colCount).map { meta.getColumnName(it) }
        appendLine(colNames.joinToString(" | "))
        appendLine("-".repeat(colNames.joinToString(" | ").length))
        while (rs.next()) {
            val row = (1..colCount).map { rs.getString(it) ?: "NULL" }
            appendLine(row.joinToString(" | "))
        }
        rs.close()
        appendLine()
    }
}
