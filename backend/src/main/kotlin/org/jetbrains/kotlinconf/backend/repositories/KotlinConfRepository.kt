package org.jetbrains.kotlinconf.backend.repositories

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.kotlinconf.FeedbackInfo
import org.jetbrains.kotlinconf.Score
import org.jetbrains.kotlinconf.SessionId
import org.jetbrains.kotlinconf.VoteInfo
import org.jetbrains.kotlinconf.backend.schema.Feedback
import org.jetbrains.kotlinconf.backend.schema.MigrationRunner
import org.jetbrains.kotlinconf.backend.schema.SignedPolicies
import org.jetbrains.kotlinconf.backend.schema.Users
import org.jetbrains.kotlinconf.backend.schema.Votes
import org.slf4j.LoggerFactory
import kotlin.time.Clock

internal class KotlinConfRepository(config: ApplicationConfig) {
    private val log = LoggerFactory.getLogger("KotlinConfRepository")

    init {
        val hikariConfig = HikariConfig()
        val dbConfig = config.config("database")
        val dbHost = dbConfig.getOrNull("host")
        val dbPoolSize = dbConfig.property("poolSize").getString().toInt()
        val database = dbConfig.getOrNull("database")

        if (dbHost?.isNotBlank() == true) {
            hikariConfig.apply {
                driverClassName = "org.postgresql.Driver"
                jdbcUrl = "jdbc:postgresql://$dbHost/$database"
                username = dbConfig.getOrNull("user")
                password = dbConfig.getOrNull("password")
                maximumPoolSize = dbPoolSize
            }
        } else {
            log.info("Host not found, using fallback")
            hikariConfig.jdbcUrl = "jdbc:h2:file:./kotlinconfg;MODE=PostgreSQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE"
            hikariConfig.validate()
        }

        log.info("Connecting to database at '${hikariConfig.jdbcUrl}")

        val connectionPool = HikariDataSource(hikariConfig)
        Database.connect(connectionPool)

        logDatabaseDiagnostics("BEFORE migrations")
        MigrationRunner.migrate()
        logDatabaseDiagnostics("AFTER migrations")
    }

    private fun logDatabaseDiagnostics(label: String) {
        transaction {
            val diagLog = LoggerFactory.getLogger("DatabaseDiagnostics")

            // List all tables in public schema with exact casing
            val tables = mutableListOf<Pair<String, String>>()
            exec(
                "SELECT table_name, table_schema FROM information_schema.tables WHERE table_schema IN ('public', 'PUBLIC') ORDER BY table_name"
            ) { rs ->
                while (rs.next()) {
                    tables.add(rs.getString("table_name") to rs.getString("table_schema"))
                }
            }
            diagLog.info("=== Database Diagnostics: $label ===")
            diagLog.info("Tables found (${tables.size}):")
            for ((name, schema) in tables) {
                diagLog.info("  table_name='$name' table_schema='$schema'")
            }

            // For each table, log row count and first 3 rows
            for ((name, _) in tables) {
                try {
                    var count = 0L
                    @Suppress("SqlSourceToSinkFlow")
                    exec("SELECT COUNT(*) AS cnt FROM \"$name\"") { rs ->
                        if (rs.next()) count = rs.getLong("cnt")
                    }

                    val rows = mutableListOf<String>()
                    @Suppress("SqlSourceToSinkFlow")
                    exec("SELECT * FROM \"$name\" LIMIT 3") { rs ->
                        val meta = rs.metaData
                        val colNames = (1..meta.columnCount).map { meta.getColumnName(it) }
                        if (rows.isEmpty()) {
                            rows.add("  columns: $colNames")
                        }
                        while (rs.next()) {
                            val values = colNames.map { col -> "$col=${rs.getString(col)}" }
                            rows.add("  row: ${values.joinToString(", ")}")
                        }
                    }

                    diagLog.info("Table '$name': $count rows")
                    for (row in rows) {
                        diagLog.info(row)
                    }
                } catch (e: Exception) {
                    diagLog.warn("Table '$name': failed to query - ${e.message}")
                }
            }
            diagLog.info("=== End Database Diagnostics: $label ===")
        }
    }

    suspend fun validateUser(uuid: String): Boolean = suspendTransaction {
        Users.selectAll().where { Users.userId eq uuid }.count() != 0L
    }

    suspend fun createUser(
        uuidValue: String, timestampValue: LocalDateTime
    ): Boolean = suspendTransaction {
        val count = Users.selectAll().where { Users.userId eq uuidValue }.count()
        if (count != 0L) return@suspendTransaction false

        Users.insert {
            it[userId] = uuidValue
            it[timestamp] = timestampValue.toString()
        }

        return@suspendTransaction true
    }

    suspend fun signPolicy(
        uuidValue: String, yearValue: Int, timestampValue: LocalDateTime
    ): Boolean = suspendTransaction {
        val count = SignedPolicies.selectAll()
            .where { (SignedPolicies.userId eq uuidValue) and (SignedPolicies.year eq yearValue) }
            .count()
        if (count != 0L) return@suspendTransaction false

        SignedPolicies.insert {
            it[userId] = uuidValue
            it[year] = yearValue
            it[timestamp] = timestampValue.toString()
        }

        return@suspendTransaction true
    }

    suspend fun getVotes(uuid: String, year: Int): List<VoteInfo> = suspendTransaction {
        Votes.selectAll().where { (Votes.userId eq uuid) and (Votes.year eq year) }
            .map { VoteInfo(it[Votes.sessionId], Score.fromValue(it[Votes.rating])) }

    }

    suspend fun getAllVotes(year: Int): List<VoteInfo> = suspendTransaction {
        Votes.selectAll()
            .where { Votes.year eq year }
            .map { VoteInfo(it[Votes.sessionId], Score.fromValue(it[Votes.rating])) }
    }

    suspend fun changeVote(
        userIdValue: String,
        sessionIdValue: SessionId,
        scoreValue: Score?,
        timestampValue: LocalDateTime,
        yearValue: Int,
    ) {
        if (scoreValue == null) {
            deleteVote(userIdValue, sessionIdValue, yearValue)
            return
        }

        suspendTransaction {
            val count = Votes.selectAll()
                .where {
                    (Votes.userId eq userIdValue) and
                            (Votes.sessionId eq sessionIdValue) and
                            (Votes.year eq yearValue)
                }
                .count()

            if (count == 0L) {
                Votes.insert {
                    it[userId] = userIdValue
                    it[sessionId] = sessionIdValue
                    it[rating] = scoreValue.value
                    it[timestamp] = timestampValue.toString()
                    it[year] = yearValue
                }
                return@suspendTransaction
            }

            Votes.update({ (Votes.userId eq userIdValue) and (Votes.sessionId eq sessionIdValue) and (Votes.year eq yearValue) }) {
                it[rating] = scoreValue.value
            }
        }
    }

    suspend fun setFeedback(
        userIdValue: String,
        sessionIdValue: SessionId,
        feedbackValue: String,
        timestampValue: LocalDateTime,
        yearValue: Int,
    ): Boolean = suspendTransaction {
        Feedback.insert {
            it[userId] = userIdValue
            it[sessionId] = sessionIdValue
            it[feedback] = feedbackValue
            it[timestamp] = timestampValue.toString()
            it[year] = yearValue
        }.insertedCount > 0
    }

    suspend fun deleteVote(uuid: String, sessionId: SessionId, year: Int) {
        suspendTransaction {
            Votes.deleteWhere { (userId eq uuid) and (Votes.sessionId eq sessionId) and (Votes.year eq year) }
        }
    }

    suspend fun getFeedbackSummary(year: Int): List<FeedbackInfo> = suspendTransaction {
        Feedback.selectAll()
            .where { Feedback.year eq year }
            .map {
                FeedbackInfo(it[Feedback.sessionId], it[Feedback.feedback])
            }
    }
}

internal fun ApplicationConfig.getOrNull(name: String): String? = kotlin.runCatching {
    property(name).getString()
}.getOrNull()
