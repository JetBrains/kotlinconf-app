package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

// TODO initialize this table from 2025 user list
internal object SignedPolicies : Table() {
    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val year: Column<Int> = integer("year")

    override val primaryKey: PrimaryKey = PrimaryKey(userId, year)
}
