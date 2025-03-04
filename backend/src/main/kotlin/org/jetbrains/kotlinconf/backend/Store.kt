package org.jetbrains.kotlinconf.backend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.backend.Votes.sessionId


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
            SchemaUtils.create(Users, Votes, Feedback, News)
        }
    }

    suspend fun validateUser(uuid: String): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        Users.selectAll().where { Users.userId eq uuid }.count() != 0L
    }

    suspend fun createUser(
        uuidValue: String, timestampValue: LocalDateTime
    ): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        val count = Users.selectAll().where { Users.userId eq uuidValue }.count()
        if (count != 0L) return@newSuspendedTransaction false

        Users.insert {
            it[userId] = uuidValue
            it[timestamp] = timestampValue.toString()
        }

        return@newSuspendedTransaction true
    }

    suspend fun getVotes(uuid: String): List<VoteInfo> = newSuspendedTransaction(Dispatchers.IO) {
        Votes.selectAll().where { Votes.userId eq uuid }
            .map { VoteInfo(it[sessionId], Score.fromValue(it[Votes.rating])) }

    }

    suspend fun getAllVotes(): List<VoteInfo> = newSuspendedTransaction(Dispatchers.IO) {
        Votes.selectAll()
            .map { VoteInfo(it[sessionId], Score.fromValue(it[Votes.rating])) }
    }

    suspend fun changeVote(
        userIdValue: String,
        sessionIdValue: SessionId,
        scoreValue: Score?,
        timestampValue: LocalDateTime
    ) {
        if (scoreValue == null) {
            deleteVote(userIdValue, sessionIdValue)
            return
        }

        newSuspendedTransaction(Dispatchers.IO) {
            val count = Votes.selectAll()
                .where { (Votes.userId eq userIdValue) and (sessionId eq sessionIdValue) }.count()

            if (count == 0L) {
                Votes.insert {
                    it[userId] = userIdValue
                    it[sessionId] = sessionIdValue
                    it[rating] = scoreValue.value
                    it[timestamp] = timestampValue.toString()
                }
                return@newSuspendedTransaction
            }

            Votes.update({ (Votes.userId eq userIdValue) and (sessionId eq sessionIdValue) }) {
                it[rating] = scoreValue.value
            }
        }

    }

    suspend fun setFeedback(
        userIdValue: String,
        sessionIdValue: SessionId,
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

    suspend fun deleteVote(uuid: String, sessionId: SessionId) {
        newSuspendedTransaction(Dispatchers.IO) {
            Votes.deleteWhere { (userId eq uuid) and (Votes.sessionId eq sessionId) }
        }
    }

    suspend fun getFeedbackSummary(): List<FeedbackInfo> = newSuspendedTransaction {
        Feedback.selectAll().map {
            FeedbackInfo(
                it[Feedback.sessionId], it[Feedback.feedback]
            )
        }
    }

    suspend fun getAllNews(): List<NewsItem> = newSuspendedTransaction(Dispatchers.IO) {
        News.selectAll().map {
            NewsItem(
                id = it[News.id],
                title = it[News.title],
                publicationDate = LocalDateTime.parse(it[News.publicationDate]),
                content = it[News.content],
                photoUrl = it[News.photoUrl]
            )
        }
    }

    suspend fun getNewsById(id: String): NewsItem? = newSuspendedTransaction(Dispatchers.IO) {
        News.selectAll().where { News.id eq id }
            .map {
                NewsItem(
                    id = it[News.id],
                    title = it[News.title],
                    publicationDate = LocalDateTime.parse(it[News.publicationDate]),
                    content = it[News.content],
                    photoUrl = it[News.photoUrl]
                )
            }
            .singleOrNull()
    }

    suspend fun createNews(request: NewsRequest): NewsItem = newSuspendedTransaction(Dispatchers.IO) {
        val id = java.util.UUID.randomUUID().toString()

        News.insert {
            it[News.id] = id
            it[title] = request.title
            it[publicationDate] = request.publicationDate.toString()
            it[content] = request.content
        }

        NewsItem(
            id = id,
            title = request.title,
            publicationDate = request.publicationDate,
            content = request.content,
            photoUrl = request.photoUrl
        )
    }

    suspend fun updateNews(id: String, request: NewsRequest): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        News.update({ News.id eq id }) {
            it[title] = request.title
            it[publicationDate] = request.publicationDate.toString()
            it[content] = request.content
        } > 0
    }

    suspend fun deleteNews(id: String): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        News.deleteWhere { News.id eq id } > 0
    }
}

private fun ApplicationConfig.getOrNull(name: String): String? = kotlin.runCatching {
    property(name).getString()
}.getOrNull()
