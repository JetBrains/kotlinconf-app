package org.jetbrains.kotlinconf.backend

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.kotlinconf.backend.Votes.index

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

    val sessionId: Column<String> = varchar("sessionId", 50)
        .index()

    val rating = integer("rating")

    override val primaryKey: PrimaryKey = PrimaryKey(userId, sessionId)
}

internal object Feedback: Table() {
    val timestamp = varchar("timestamp", 50)

    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val sessionId: Column<String> = varchar("sessionId", 50)
        .index()

    val feedback: Column<String> = varchar("feedback", length = 5000)

    override val primaryKey: PrimaryKey = PrimaryKey(Votes.userId, Votes.sessionId)
}