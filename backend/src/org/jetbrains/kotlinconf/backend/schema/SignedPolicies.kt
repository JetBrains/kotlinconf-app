// ABOUTME: Exposed table definition for signed privacy policies per user per year.
// ABOUTME: Tracks which users have accepted policies for each conference year.

package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

internal object SignedPolicies : Table() {
    val userId: Column<String> = varchar("uuid", 50)

    val timestamp: Column<String> = varchar("timestamp", 50)

    val year: Column<Int> = integer("year")

    override val primaryKey: PrimaryKey = PrimaryKey(userId, year)
}
