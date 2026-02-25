package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinconf.utils.Logger
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OkioAssetStorageTest {

    private fun emptyLogger() = object : Logger {
        override fun log(tag: String, lazyMessage: () -> String) {}
    }

    private fun createStorage(tempDir: File): OkioAssetStorage {
        return OkioAssetStorage(
            year = 2025,
            cacheDir = tempDir.absolutePath,
            logger = emptyLogger(),
        )
    }

    @Test
    fun write_and_read_simple_filename() = runTest {
        val tempDir = Files.createTempDirectory("okio-test").toFile()
        try {
            val storage = createStorage(tempDir)
            storage.write("testfile.md", "hello world")
            assertEquals("hello world", storage.read("testfile.md"))
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun write_and_read_nested_path() = runTest {
        val tempDir = Files.createTempDirectory("okio-test").toFile()
        try {
            val storage = createStorage(tempDir)
            storage.write("documents/code-of-conduct.md", "# Code of Conduct")
            assertEquals("# Code of Conduct", storage.read("documents/code-of-conduct.md"))
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun files_are_stored_under_year_folder() = runTest {
        val tempDir = Files.createTempDirectory("okio-test").toFile()
        try {
            val storage = createStorage(tempDir)
            storage.write("testfile.md", "content")

            val yearDir = File(tempDir, "2025")
            assertTrue(yearDir.exists(), "Year directory should exist")
            assertTrue(File(yearDir, "testfile.md").exists(), "File should be inside year directory")
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun read_nonexistent_key_returns_null() = runTest {
        val tempDir = Files.createTempDirectory("okio-test").toFile()
        try {
            val storage = createStorage(tempDir)
            assertNull(storage.read("nonexistent.md"))
        } finally {
            tempDir.deleteRecursively()
        }
    }
}
