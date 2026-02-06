package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.observable.makeObservable
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinconf.utils.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MultiplatformSettingsStorageMigrationTest {

    @OptIn(ExperimentalSettingsApi::class)
    private fun inMemorySettings(): ObservableSettings = MapSettings().makeObservable()

    private fun emptyLogger() = object : Logger {
        override fun log(tag: String, lazyMessage: () -> String) {}
    }

    /**
     * Creates a storage object with data matching the older, 2025 storage version.
     */
    private fun get2025Settings() = inMemorySettings().apply {
        this["storageVersion"] = MultiplatformSettingsStorage.V2025
        this["newsCache"] = "{\"dummy\":\"value\"}"
        this["notificationSettings"] = """
            {
              "sessionReminders": true,
              "scheduleUpdates": false,
              "jetBrainsNews": true,
              "kotlinConfNews": "true"
            }
        """.trimIndent()
    }

    /**
     * Creates a storage object with data matching the 2026 storage version (pre year-based split).
     */
    private fun get2026Settings() = inMemorySettings().apply {
        this["storageVersion"] = MultiplatformSettingsStorage.V2026
        this["userId2025"] = "user-123"
        this["pendingUserId2025"] = "pending-456"
        this["conferenceCache"] = "{\"sessions\":[],\"speakers\":[]}"
        this["conferenceInfoCache"] = "{\"dummy\":\"info\"}"
        this["favorites"] = "[\"S1\",\"S2\"]"
        this["notificationSettings"] = """{"sessionReminders":true,"scheduleUpdates":false}"""
        this["votes"] = """[{"sessionId":{"id":"S1"},"score":"GOOD"}]"""
        this["theme"] = "DARK"
        this["flags"] = """{"debugLogging":true}"""
    }

    @Test
    fun migration_2025_to_current_updates_version() {
        val settings = get2025Settings()
        val storage = MultiplatformSettingsStorage(settings, emptyLogger())

        // Run migrations
        storage.ensureCurrentVersion()

        // Storage version should be successfully updated to the current version
        assertEquals(MultiplatformSettingsStorage.LATEST_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }

    @Test
    fun migration_2025_to_2026_removes_news_cache() {
        val settings = get2025Settings()
        val storage = MultiplatformSettingsStorage(settings, emptyLogger())

        // Run migrations
        storage.ensureCurrentVersion()

        // The newsCache key should be removed by the migration
        assertNull(settings.getStringOrNull("newsCache"))
    }

    @Test
    fun migration_2026_to_2026_001_moves_year_specific_data() {
        val settings = get2026Settings()
        val storage = MultiplatformSettingsStorage(settings, emptyLogger())

        // Run migrations
        storage.ensureCurrentVersion()

        // Old keys should be removed
        assertNull(settings.getStringOrNull("userId2025"))
        assertNull(settings.getStringOrNull("pendingUserId2025"))
        assertNull(settings.getStringOrNull("conferenceCache"))
        assertNull(settings.getStringOrNull("conferenceInfoCache"))
        assertNull(settings.getStringOrNull("favorites"))
        assertNull(settings.getStringOrNull("notificationSettings"))
        assertNull(settings.getStringOrNull("votes"))

        // New year-prefixed keys should exist
        assertEquals("user-123", settings.getStringOrNull("2026_userId"))
        assertEquals("pending-456", settings.getStringOrNull("2026_pendingUserId"))
        assertNotNull(settings.getStringOrNull("2026_conferenceCache"))
        assertNotNull(settings.getStringOrNull("2026_conferenceInfoCache"))
        assertEquals("[\"S1\",\"S2\"]", settings.getStringOrNull("2026_favorites"))
        assertNotNull(settings.getStringOrNull("2026_notificationSettings"))
        assertNotNull(settings.getStringOrNull("2026_votes"))

        // Global keys should still be present
        assertEquals("DARK", settings.getStringOrNull("theme"))
        assertNotNull(settings.getStringOrNull("flags"))
    }

    @Test
    fun migration_2026_to_2026_001_preserves_global_data() = runTest {
        val settings = get2026Settings()
        val storage = MultiplatformSettingsStorage(settings, emptyLogger())

        // Run migrations
        storage.ensureCurrentVersion()

        // Global data should still be accessible through the storage interface
        val theme = storage.getTheme().first()
        assertEquals(org.jetbrains.kotlinconf.Theme.DARK, theme)

        val flags = storage.getFlags().first()
        assertNotNull(flags)
        assertTrue(flags.debugLogging)
    }

    @Test
    fun unknown_storage_version_triggers_destructive_upgrade_and_clears_all_keys() {
        val settings = inMemorySettings()
        settings["userId2025"] = "user-123"
        settings["newsCache"] = "legacy-data"
        settings["notificationSettings"] = "{\"sessionReminders\":false,\"scheduleUpdates\":true}"
        val storage = MultiplatformSettingsStorage(settings, emptyLogger())

        // Run migrations
        storage.ensureCurrentVersion()

        // Only the storageVersion should remain and be set to the current version
        assertEquals(setOf("storageVersion"), settings.keys)
        assertEquals(MultiplatformSettingsStorage.LATEST_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }

    @Test
    fun unknown_migration_path_triggers_destructive_upgrade() {
        val settings = inMemorySettings()
        settings["storageVersion"] = 2024_000 // A version we don't have a migration for
        settings["favorites"] = "[\"S1\",\"S2\"]"
        val storage = MultiplatformSettingsStorage(settings, emptyLogger())

        // Run migrations
        storage.ensureCurrentVersion()

        // Only the storageVersion should remain and be set to the current version
        assertEquals(setOf("storageVersion"), settings.keys)
        assertEquals(MultiplatformSettingsStorage.LATEST_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }
}
