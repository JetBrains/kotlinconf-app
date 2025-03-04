package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

internal object News : Table() {
    val id: Column<String> = varchar("id", 50)
    val title: Column<String> = varchar("title", 200)
    val publicationDate: Column<String> = varchar("publication_date", 50)
    val content: Column<String> = text("content")
    val photoUrl: Column<String> = text("photo_url")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}