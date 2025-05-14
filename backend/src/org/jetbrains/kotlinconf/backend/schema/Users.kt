package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

internal object Users : Table() {
    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val timestamp: Column<String> = varchar("timestamp", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}