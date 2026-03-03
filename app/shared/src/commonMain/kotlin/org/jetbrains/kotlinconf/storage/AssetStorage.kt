package org.jetbrains.kotlinconf.storage

interface AssetStorage {
    suspend fun read(key: String): String?
    suspend fun write(key: String, content: String)
}
