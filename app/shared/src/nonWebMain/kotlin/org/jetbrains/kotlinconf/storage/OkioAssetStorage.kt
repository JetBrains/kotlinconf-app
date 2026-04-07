package org.jetbrains.kotlinconf.storage

import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import org.jetbrains.kotlinconf.di.FileStorageDir
import org.jetbrains.kotlinconf.di.Year
import org.jetbrains.kotlinconf.di.YearScope
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged

@ContributesBinding(YearScope::class)
@SingleIn(YearScope::class)
class OkioAssetStorage(
    @Year year: Int,
    @FileStorageDir cacheDir: String,
    logger: Logger,
) : AssetStorage {
    private val taggedLogger = logger.tagged("OkioFileCache")
    private val fileSystem = FileSystem.SYSTEM
    private val baseDir: Path = cacheDir.toPath() / year.toString()

    init {
        fileSystem.createDirectories(baseDir)
    }

    private fun filePath(key: String): Path {
        require(key.matches(VALID_KEY_REGEX)) {
            "Invalid file cache key: '$key'."
        }
        return baseDir / key
    }

    override suspend fun read(key: String): String? = withContext(Dispatchers.IO) {
        try {
            val path = filePath(key)
            if (fileSystem.exists(path)) {
                fileSystem.read(path) { readUtf8() }
            } else {
                null
            }
        } catch (_: IOException) {
            null
        }
    }

    override suspend fun write(key: String, content: String): Unit = withContext(Dispatchers.IO) {
        taggedLogger.log { "Writing file with key '$key'" }
        val path = filePath(key)
        val parent = path.parent
        if (parent == null) {
            taggedLogger.log { "File has no parent! Path was $path" }
            return@withContext
        }

        fileSystem.createDirectories(parent)
        fileSystem.write(path) { writeUtf8(content) }
    }

    companion object {
        private val VALID_KEY_REGEX = Regex("^[a-zA-Z0-9/._-]+$")
    }
}
