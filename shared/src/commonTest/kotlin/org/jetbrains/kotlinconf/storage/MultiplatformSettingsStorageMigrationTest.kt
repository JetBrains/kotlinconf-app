package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.observable.makeObservable
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.NotificationSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MultiplatformSettingsStorageMigrationTest {

    @OptIn(ExperimentalSettingsApi::class)
    private fun inMemorySettings(): ObservableSettings = MapSettings().makeObservable()

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

    @Test
    fun migration_2025_to_2026_updates_version() {
        val settings = get2025Settings()
        val storage = MultiplatformSettingsStorage(settings)

        // Run migrations
        storage.ensureCurrentVersion()

        // Storage version should be successfully updated to the current version
        assertEquals(MultiplatformSettingsStorage.CURRENT_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }

    @Test
    fun migration_2026_to_2026_removes_news_cache() {
        val settings = get2025Settings()
        val storage = MultiplatformSettingsStorage(settings)

        // Run migrations
        storage.ensureCurrentVersion()

        // The newsCache key should be removed by the migration
        assertNull(settings.getStringOrNull("newsCache"))
    }

    @Test
    fun migration_2026_to_2026_migrates_notification_settings() = runTest {
        val settings = get2025Settings()
        val storage = MultiplatformSettingsStorage(settings)

        // Run migrations
        storage.ensureCurrentVersion()

        // Notification settings should still be readable with the current version
        val notiSettings = storage.getNotificationSettings().first()
        assertTrue(notiSettings != null)
    }

    @Test
    fun unknown_storage_version_triggers_destructive_upgrade_and_clears_all_keys() {
        val settings = inMemorySettings()
        settings["userId2025"] = "user-123"
        settings["newsCache"] = "legacy-data"
        settings["notificationSettings"] = "{\"sessionReminders\":false,\"scheduleUpdates\":true}"
        val storage = MultiplatformSettingsStorage(settings)

        // Run migrations
        storage.ensureCurrentVersion()

        // Only the storageVersion should remain and be set to the current version
        assertEquals(setOf("storageVersion"), settings.keys)
        assertEquals(MultiplatformSettingsStorage.CURRENT_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }

    @Test
    fun unknown_migration_path_triggers_destructive_upgrade() {
        val settings = inMemorySettings()
        settings["storageVersion"] = 2024_000 // A version we don't have a migration for
        settings["favorites"] = "[\"S1\",\"S2\"]"
        val storage = MultiplatformSettingsStorage(settings)

        // Run migrations
        storage.ensureCurrentVersion()

        // Only the storageVersion should remain and be set to the current version
        assertEquals(setOf("storageVersion"), settings.keys)
        assertEquals(MultiplatformSettingsStorage.CURRENT_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }
}
