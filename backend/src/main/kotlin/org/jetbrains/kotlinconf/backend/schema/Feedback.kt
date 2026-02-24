package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.kotlinconf.SessionId

internal object Feedback : Table() {
    val timestamp = varchar("timestamp", 50)

    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val sessionId: Column<SessionId> = varchar("sessionId", 50)
        .transform(SessionIdTransformer)
        .index()

    val feedback: Column<String> = varchar("feedback", length = 5000)

    val year: Column<Int> = integer("year")

    override val primaryKey: PrimaryKey = PrimaryKey(userId, sessionId, year)
}
