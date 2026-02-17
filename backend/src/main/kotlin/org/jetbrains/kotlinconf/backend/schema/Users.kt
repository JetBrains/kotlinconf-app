package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

internal object Users : Table() {
    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val timestamp: Column<String> = varchar("timestamp", 50)

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}