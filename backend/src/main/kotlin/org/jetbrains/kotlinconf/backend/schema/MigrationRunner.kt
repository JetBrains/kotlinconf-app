// ABOUTME: SQL-based database migration runner that discovers and applies numbered .sql files.
// ABOUTME: Tracks applied migrations in a schema_migrations table for idempotent startup.

package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI
import java.util.jar.JarFile

data class Migration(val version: Int, val name: String, val sql: String)

private object SchemaMigrations : Table("schema_migrations") {
    val version = integer("version")
    val name = varchar("name", 255)
    val appliedAt = varchar("applied_at", 50)
    override val primaryKey = PrimaryKey(version)
}

object MigrationRunner {
    private val log = LoggerFactory.getLogger("MigrationRunner")

    fun migrate(database: Database? = null) {
        transaction(database) {
            SchemaUtils.create(SchemaMigrations)
        }

        val applied = appliedVersions(database)
        val migrations = discoverMigrations()

        for (migration in migrations) {
            if (migration.version !in applied) {
                log.info("Applying migration V{}: {}", migration.version, migration.name)
                transaction(database) {
                    val statements = migration.sql
                        .lines()
                        .filter { !it.trimStart().startsWith("--") }
                        .joinToString("\n")
                        .split(";")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }

                    for (statement in statements) {
                        // Exposed's Transaction.exec runs a raw SQL statement
                        @Suppress("SqlSourceToSinkFlow")
                        exec(statement)
                    }

                    SchemaMigrations.insert {
                        it[version] = migration.version
                        it[name] = migration.name
                        it[appliedAt] = java.time.Instant.now().toString()
                    }
                }
                log.info("Applied migration V{}: {}", migration.version, migration.name)
            }
        }
    }

    fun currentVersion(database: Database? = null): Int {
        return transaction(database) {
            SchemaUtils.create(SchemaMigrations)
            SchemaMigrations.selectAll().map { it[SchemaMigrations.version] }.maxOrNull() ?: 0
        }
    }

    fun discoverMigrations(): List<Migration> {
        val resourcePath = "db/migrations"
        val classLoader = Thread.currentThread().contextClassLoader
        val pattern = Regex("""V(\d+)__(.+)\.sql""")

        val url = classLoader.getResource(resourcePath) ?: return []

        val filenames: List<String> = when (url.protocol) {
            "file" -> File(url.toURI()).list()?.toList() ?: []
            "jar" -> {
                val jarPath = url.path.substringBefore("!")
                JarFile(URI(jarPath).path).use { jar ->
                    jar.entries().asSequence()
                        .map { it.name }
                        .filter { it.startsWith("$resourcePath/") && it.endsWith(".sql") }
                        .map { it.substringAfterLast("/") }
                        .toList()
                }
            }
            else -> []
        }

        return filenames.mapNotNull { filename ->
            pattern.matchEntire(filename)?.let { match ->
                val version = match.groupValues[1].toInt()
                val name = match.groupValues[2]
                val sql = classLoader.getResource("$resourcePath/$filename")!!.readText()
                Migration(version, name, sql)
            }
        }.sortedBy { it.version }
    }

    internal fun appliedVersions(database: Database? = null): Set<Int> {
        return transaction(database) {
            SchemaMigrations.selectAll().map { it[SchemaMigrations.version] }.toSet()
        }
    }
}
