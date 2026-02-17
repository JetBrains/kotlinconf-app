package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

internal object SignedPolicies : Table() {
    val userId: Column<String> = varchar("uuid", 50)
        .index()

    val year: Column<Int> = integer("year")

    override val primaryKey: PrimaryKey = PrimaryKey(userId, year)
}
