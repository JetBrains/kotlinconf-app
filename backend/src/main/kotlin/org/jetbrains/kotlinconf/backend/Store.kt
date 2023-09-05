package org.jetbrains.kotlinconf.backend

import com.zaxxer.hikari.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.backend.Votes.rating
import org.jetbrains.kotlinconf.backend.Votes.sessionId
import java.time.*


internal class Store(application: Application) {

    init {
        val hikariConfig = HikariConfig()
        val dbConfig = application.environment.config.config("database")
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
            application.log.info("Host not found, using fallback")
            hikariConfig.jdbcUrl = "jdbc:h2:file:./kotlinconfg"
            hikariConfig.validate()
        }

        application.log.info("Connecting to database at '${hikariConfig.jdbcUrl}")

        val connectionPool = HikariDataSource(hikariConfig)
        Database.connect(connectionPool)

        transaction {
            SchemaUtils.create(Users, Votes, Feedback)
        }
    }

    suspend fun validateUser(uuid: String): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        Users.select { Users.userId eq uuid }.count() != 0L
    }

    suspend fun createUser(
        uuidValue: String, timestampValue: LocalDateTime
    ): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val count = Users.select { Users.userId eq uuidValue }.count()
        if (count != 0L) return@newSuspendedTransaction false

        Users.insert {
            it[userId] = uuidValue
            it[timestamp] = timestampValue.toString()
        }

        return@newSuspendedTransaction true
    }

    suspend fun usersCount(): Long = newSuspendedTransaction(Dispatchers.IO) {
        Users.selectAll().count()
    }

    suspend fun getVotes(uuid: String): List<VoteInfo> = newSuspendedTransaction(Dispatchers.IO) {
        Votes.select { Votes.userId eq uuid }
            .map { VoteInfo(it[Votes.sessionId], Score.fromValue(it[Votes.rating])) }

    }

    suspend fun getAllVotes(): List<VoteInfo> = newSuspendedTransaction(Dispatchers.IO) {
        Votes.selectAll()
            .map { VoteInfo(it[Votes.sessionId], Score.fromValue(it[Votes.rating])) }
    }

    suspend fun changeVote(
        userIdValue: String,
        sessionIdValue: String,
        scoreValue: Score?,
        timestampValue: LocalDateTime
    ) {
        if (scoreValue == null) {
            deleteVote(userIdValue, sessionIdValue)
            return
        }

        newSuspendedTransaction(Dispatchers.IO) {
            val count = Votes.select {
                (Votes.userId eq userIdValue) and (Votes.sessionId eq sessionIdValue)
            }.count()

            if (count == 0L) {
                Votes.insert {
                    it[userId] = userIdValue
                    it[sessionId] = sessionIdValue
                    it[rating] = scoreValue.value
                    it[timestamp] = timestampValue.toString()
                }
                return@newSuspendedTransaction
            }

            Votes.update({ (Votes.userId eq userIdValue) and (Votes.sessionId eq sessionIdValue) }) {
                it[rating] = scoreValue.value
            }
        }

    }

    suspend fun setFeedback(
        userIdValue: String,
        sessionIdValue: String,
        feedbackValue: String,
        timestampValue: LocalDateTime
    ): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        Feedback.insert {
            it[userId] = userIdValue
            it[sessionId] = sessionIdValue
            it[feedback] = feedbackValue
            it[timestamp] = timestampValue.toString()
        }.insertedCount > 0
    }

    suspend fun deleteVote(uuid: String, sessionId: String) {
        newSuspendedTransaction(Dispatchers.IO) {
            Votes.deleteWhere { (userId eq uuid) and (Votes.sessionId eq sessionId) }
        }
    }

    suspend fun getVotesSummary(): Map<String, VoteInfo> = newSuspendedTransaction(Dispatchers.IO) {
        val result: List<String> = Votes.slice(Votes.sessionId, Votes.rating, Votes.rating.count()).selectAll()
            .groupBy(Votes.sessionId, Votes.rating)
            .map {
                it[sessionId]
            }

        TODO()
    }

    suspend fun getFeedbackSummary(): List<FeedbackInfo> = newSuspendedTransaction {
        Feedback.selectAll().map {
            FeedbackInfo(
                it[Feedback.sessionId], it[Feedback.feedback]
            )
        }
    }
}


private fun ApplicationConfig.getOrNull(name: String): String? = kotlin.runCatching {
    property(name).getString()
}.getOrNull()