package org.jetbrains.kotlinconf.backend.schema

import org.jetbrains.exposed.sql.ColumnTransformer
import org.jetbrains.kotlinconf.SessionId

internal object SessionIdTransformer : ColumnTransformer<String, SessionId> {
    override fun unwrap(value: SessionId): String = value.id
    override fun wrap(value: String): SessionId = SessionId(value)
}