package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.observable.makeObservable
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.jetbrains.kotlinconf.utils.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApplicationStorageImplMigrationTest {

    @OptIn(ExperimentalSettingsApi::class)
    private fun inMemorySettings(): ObservableSettings = MapSettings().makeObservable()

    private fun emptyLogger() = object : Logger {
        override fun log(tag: String, lazyMessage: () -> String) {}
    }

    private fun createStorage(settings: ObservableSettings, scope: TestScope) =
        ApplicationStorageImpl(settings, scope.backgroundScope, emptyLogger())

    /**
     * Creates a storage object with data matching the older, 2025 storage version.
     */
    private fun get2025Settings() = inMemorySettings().apply {
        this["storageVersion"] = ApplicationStorageImpl.V2025
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
        this["storageVersion"] = ApplicationStorageImpl.V2026
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
    fun migration_2025_to_current_updates_version() = runTest {
        val settings = get2025Settings()
        val storage = createStorage(settings, this)

        // Run migrations
        storage.initialize()

        // Storage version should be successfully updated to the current version
        assertEquals(ApplicationStorageImpl.LATEST_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }

    @Test
    fun migration_2025_to_2026_removes_news_cache() = runTest {
        val settings = get2025Settings()
        val storage = createStorage(settings, this)

        // Run migrations
        storage.initialize()

        // The newsCache key should be removed by the migration
        assertNull(settings.getStringOrNull("newsCache"))
    }

    @Test
    fun migration_2026_to_2026_001_moves_year_specific_data() = runTest {
        val settings = get2026Settings()
        val storage = createStorage(settings, this)

        // Run migrations
        storage.initialize()

        // Old keys should be removed
        assertNull(settings.getStringOrNull("userId2025"))
        assertNull(settings.getStringOrNull("pendingUserId2025"))
        assertNull(settings.getStringOrNull("conferenceCache"))
        assertNull(settings.getStringOrNull("conferenceInfoCache"))
        assertNull(settings.getStringOrNull("favorites"))
        assertNull(settings.getStringOrNull("notificationSettings"))
        assertNull(settings.getStringOrNull("votes"))

        // userId is migrated to the global key, not year-prefixed
        assertEquals("user-123", settings.getStringOrNull("userId"))
        // pendingUserId is removed, not migrated
        assertNull(settings.getStringOrNull("2025_pendingUserId"))

        // Year-specific keys are migrated
        assertNotNull(settings.getStringOrNull("2025_conferenceCache"))
        assertNotNull(settings.getStringOrNull("2025_conferenceInfoCache"))
        assertEquals("[\"S1\",\"S2\"]", settings.getStringOrNull("2025_favorites"))
        assertNotNull(settings.getStringOrNull("2025_notificationSettings"))
        assertNotNull(settings.getStringOrNull("2025_votes"))

        // Global keys should still be present
        assertEquals("DARK", settings.getStringOrNull("theme"))
        assertNotNull(settings.getStringOrNull("flags"))
    }

    @Test
    fun migration_2026_to_2026_001_preserves_global_data() = runTest {
        val settings = get2026Settings()
        val storage = createStorage(settings, this)

        // Run migrations
        storage.initialize()

        // Global data should still be accessible through the storage interface
        val theme = storage.getTheme().first()
        assertEquals(org.jetbrains.kotlinconf.Theme.DARK, theme)

        val flags = storage.getFlags().first()
        assertNotNull(flags)
        assertTrue(flags.debugLogging)
    }

    @Test
    fun unknown_storage_version_triggers_destructive_upgrade_and_clears_all_keys() = runTest {
        val settings = inMemorySettings()
        settings["userId2025"] = "user-123"
        settings["newsCache"] = "legacy-data"
        settings["notificationSettings"] = "{\"sessionReminders\":false,\"scheduleUpdates\":true}"
        val storage = createStorage(settings, this)

        // Run migrations
        storage.initialize()

        // storageVersion and userId should always exist after init
        assertEquals(setOf("storageVersion", "userId"), settings.keys)
        assertEquals(ApplicationStorageImpl.LATEST_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }

    @Test
    fun unknown_migration_path_triggers_destructive_upgrade() = runTest {
        val settings = inMemorySettings()
        settings["storageVersion"] = 2024_000 // A version we don't have a migration for
        settings["favorites"] = "[\"S1\",\"S2\"]"
        val storage = createStorage(settings, this)

        // Run migrations
        storage.initialize()

        // storageVersion and userId should always exist after init
        assertEquals(setOf("storageVersion", "userId"), settings.keys)
        assertEquals(ApplicationStorageImpl.LATEST_STORAGE_VERSION, settings.getInt("storageVersion", 0))
    }
}
