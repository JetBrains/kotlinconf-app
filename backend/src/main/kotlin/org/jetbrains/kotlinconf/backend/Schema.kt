package org.jetbrains.kotlinconf.backend

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnTransformer
import org.jetbrains.exposed.sql.Table
import org.jetbrains.kotlinconf.SessionId

internal object Users : Table() {
    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val timestamp: Column<String> = varchar("timestamp", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}

internal object Votes : Table() {
    val timestamp = varchar("timestamp", 50)

    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val sessionId: Column<SessionId> = varchar("sessionId", 50)
        .transform(SessionIdTransformer)
        .index()

    val rating = integer("rating")

    override val primaryKey: PrimaryKey = PrimaryKey(userId, sessionId)
}

internal object Feedback: Table() {
    val timestamp = varchar("timestamp", 50)

    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val sessionId: Column<SessionId> = varchar("sessionId", 50)
        .transform(SessionIdTransformer)
        .index()

    val feedback: Column<String> = varchar("feedback", length = 5000)

    override val primaryKey: PrimaryKey = PrimaryKey(Votes.userId, Votes.sessionId)
}

internal object News : Table() {
    val id: Column<String> = varchar("id", 50)
    val title: Column<String> = varchar("title", 200)
    val publicationDate: Column<String> = varchar("publication_date", 50)
    val content: Column<String> = text("content")
    val photoUrl: Column<String> = text("photo_url")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

private object SessionIdTransformer : ColumnTransformer<String, SessionId> {
    override fun unwrap(value: SessionId): String = value.id
    override fun wrap(value: String): SessionId = SessionId(value)
}
