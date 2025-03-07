package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.kotlinconf.SessionId

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