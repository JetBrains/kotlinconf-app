package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("Migrations")

internal fun initializeDatabase() {
    transaction {
        migrateYearColumns()
        SchemaUtils.create(Users, SignedPolicies, Votes, Feedback)
        backfillSignedPolicies()
        validateSchema()
    }
}

private fun migrateYearColumns() {
    log.info("Running year column migrations")

    val existingTables = SchemaUtils.listTables()

    fun Table.existsInDb() = existingTables.any { it == tableName }

    if (Votes.existsInDb()) {
        migrateYearColumn(Votes)
    }
    if (Feedback.existsInDb()) {
        migrateYearColumn(Feedback)
    }

    log.info("Done year column migrations")
}

private fun migrateYearColumn(table: Table) {
    log.info("Running year column migration for $table")

    val tableName = table.tableName
    val pkName = "pk_$tableName"
    val pkColumns = table.primaryKey!!.columns.joinToString(", ") { "\"${it.name}\"" }

    exec("""ALTER TABLE "$tableName" ADD COLUMN IF NOT EXISTS "year" INT""")
    exec("""UPDATE "$tableName" SET "year" = 2025 WHERE "year" IS NULL""")
    exec("""ALTER TABLE "$tableName" ALTER COLUMN "year" SET NOT NULL""")
    exec("""ALTER TABLE "$tableName" DROP CONSTRAINT IF EXISTS "$pkName"""")
    exec("""ALTER TABLE "$tableName" ADD CONSTRAINT "$pkName" PRIMARY KEY ($pkColumns)""")

    log.info("Done year column migration for $table")
}

private fun backfillSignedPolicies() {
    log.info("Running SignedPolicies backfill")

    exec("""
        INSERT INTO "${SignedPolicies.tableName}" ("${SignedPolicies.userId.name}", "${SignedPolicies.year.name}")
        SELECT "${Users.userId.name}", 2025
        FROM "${Users.tableName}"
        WHERE "${Users.userId.name}" NOT IN (
            SELECT "${SignedPolicies.userId.name}"
            FROM "${SignedPolicies.tableName}"
            WHERE "${SignedPolicies.year.name}" = 2025
        )
    """.trimIndent())

    log.info("Done SignedPolicies backfill")
}

private fun validateSchema() {
    try {
        val statements = MigrationUtils.statementsRequiredForDatabaseMigration(Users, SignedPolicies, Votes, Feedback)
        if (statements.isEmpty()) {
            log.info("Schema validation successful")
        } else {
            log.warn("Schema validation found remaining diffs after migration:")
            statements.forEach { log.warn("  $it") }
        }
    } catch (e: Exception) {
        log.warn("Schema validation failed: ${e.message}")
    }
}

private fun exec(sql: String) {
    TransactionManager.current().exec(sql)
}
