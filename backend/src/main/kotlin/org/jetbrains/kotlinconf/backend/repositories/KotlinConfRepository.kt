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
            hikariConfig.jdbcUrl = "jdbc:h2:file:./kotlinconfg"
            hikariConfig.validate()
        }

        log.info("Connecting to database at '${hikariConfig.jdbcUrl}")

        val connectionPool = HikariDataSource(hikariConfig)
        Database.connect(connectionPool)

        MigrationRunner.migrate()
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
                .where { (Votes.userId eq userIdValue) and (Votes.sessionId eq sessionIdValue) and (Votes.year eq yearValue) }.count()

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
    ): Boolean = suspendTransaction {
        Feedback.insert {
            it[userId] = userIdValue
            it[sessionId] = sessionIdValue
            it[feedback] = feedbackValue
            it[timestamp] = timestampValue.toString()
        }.insertedCount > 0
    }

    suspend fun deleteVote(uuid: String, sessionId: SessionId, year: Int) {
        suspendTransaction {
            Votes.deleteWhere { (userId eq uuid) and (Votes.sessionId eq sessionId) and (Votes.year eq year) }
        }
    }

    suspend fun getFeedbackSummary(): List<FeedbackInfo> = suspendTransaction {
        Feedback.selectAll().map {
            FeedbackInfo(
                it[Feedback.sessionId], it[Feedback.feedback]
            )
        }
    }
}

internal fun ApplicationConfig.getOrNull(name: String): String? = kotlin.runCatching {
    property(name).getString()
}.getOrNull()
