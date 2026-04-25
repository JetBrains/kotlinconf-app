package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.AppConfig
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.NotificationSettings
import org.jetbrains.kotlinconf.Theme
import org.jetbrains.kotlinconf.getPlatformId
import org.jetbrains.kotlinconf.utils.Logger
import org.jetbrains.kotlinconf.utils.tagged
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@OptIn(ExperimentalSettingsApi::class)
class ApplicationStorageImpl(
    private val settings: ObservableSettings,
    appScope: CoroutineScope,
    logger: Logger,
) : ApplicationStorage {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val taggedLogger = logger.tagged("ApplicationStorageImpl")

    private inline fun <reified T> String?.decodeOrNull(): T? {
        if (this == null) return null
        return try {
            json.decodeFromString<T>(this)
        } catch (_: SerializationException) {
            null
        }
    }

    override val userId = settings
        .getStringFlow(Keys.USER_ID, "")
        .stateIn(appScope, SharingStarted.Eagerly, "")

    override fun isOnboardingComplete(): Flow<Boolean> = settings.getBooleanFlow(Keys.ONBOARDING_COMPLETE, false)
    override suspend fun setOnboardingComplete(value: Boolean) = settings.set(Keys.ONBOARDING_COMPLETE, value)

    override fun getTheme(): Flow<Theme> = settings.getStringOrNullFlow(Keys.THEME).map { Theme.entries.firstOrNull { entry -> entry.name == it } ?: Theme.SYSTEM }
    override suspend fun setTheme(value: Theme) = settings.set(Keys.THEME, value.name)

    override fun getFlagsBlocking(): Flags? = settings.getStringOrNull(Keys.FLAGS)?.decodeOrNull<Flags>()
    override fun getFlags(): Flow<Flags?> = settings.getStringOrNullFlow(Keys.FLAGS).map { it.decodeOrNull<Flags>() }
    override suspend fun setFlags(value: Flags) = settings.set(Keys.FLAGS, json.encodeToString(value))

    override fun getConfig(): Flow<AppConfig?> = settings.getStringOrNullFlow(Keys.CONFIG).map { it.decodeOrNull<AppConfig>() ?: DEFAULT_CONFIG }
    override suspend fun setConfig(config: AppConfig) = settings.set(Keys.CONFIG, json.encodeToString(config))

    override fun initialize() {
        ensureCurrentVersion()
        ensureUserId()
    }

    private fun ensureCurrentVersion() {
        var version = settings.getInt(Keys.STORAGE_VERSION, 0)

        taggedLogger.log { "Storage version is $version" }

        if (version == 0) {
            taggedLogger.log { "Unknown previous storage version, performing destructive migration" }
            destructiveUpgrade()
            return
        }

        if (version > LATEST_STORAGE_VERSION) {
            taggedLogger.log { "Storage version not recognized, performing destructive migration" }
            destructiveUpgrade()
            return
        }

        if (version == LATEST_STORAGE_VERSION) {
            taggedLogger.log { "Storage version matches expected version, no need to migrate" }
            return
        }

        while (version < LATEST_STORAGE_VERSION) {
            taggedLogger.log { "Finding migrations from $version to $LATEST_STORAGE_VERSION..." }

            // Find a migration from the current version that takes us as far forward as possible
            val nextMigration = migrations.filter { it.from == version }.maxByOrNull { it.to }
            if (nextMigration == null) {
                taggedLogger.log { "No matching migrations found" }

                // Failed to find a migration path to latest, fall back to destructive
                destructiveUpgrade()
                return
            }

            taggedLogger.log { "Running migration from ${nextMigration.from} to ${nextMigration.to}" }

            nextMigration.migrate()
            version = nextMigration.to
            settings.set(Keys.STORAGE_VERSION, version)

            taggedLogger.log { "Successfully migrated to $version" }
        }
    }

    private fun ensureUserId() {
        val existingUserId = settings.getString(Keys.USER_ID, "")
        if (existingUserId.isBlank()) {
            @OptIn(ExperimentalUuidApi::class)
            val generatedUserId = "${getPlatformId()}-${Uuid.random()}"
            settings.set(Keys.USER_ID, generatedUserId)
        }
    }

    private fun destructiveUpgrade() {
        taggedLogger.log { "Performing destructive upgrade to $LATEST_STORAGE_VERSION" }
        settings.clear()
        settings.set(Keys.STORAGE_VERSION, LATEST_STORAGE_VERSION)
    }

    private data class Migration(val from: Int, val to: Int, val migrate: () -> Unit)

    private val migrations = [
        Migration(V2025, V2026) {
            // News were removed
            settings.remove(OldKeys.NEWS_CACHE)
            // We removed news fields from NotificationSettings, read/write it once to update
            settings.getStringOrNull(OldKeys.NOTIFICATION_SETTINGS)
                ?.decodeOrNull<NotificationSettings>()
                ?.let { settings.set(OldKeys.NOTIFICATION_SETTINGS, json.encodeToString(it)) }
        },
        Migration(V2026, V2026_001) {
            // Migrate year-specific data that was created during 2025
            val year = 2025
            migrateKey(OldKeys.USER_ID, Keys.USER_ID)
            settings.remove(OldKeys.PENDING_USER_ID)
            migrateKey(OldKeys.CONFERENCE_CACHE, "${year}_conferenceCache")
            migrateKey(OldKeys.CONFERENCE_INFO_CACHE, "${year}_conferenceInfoCache")
            migrateKey(OldKeys.FAVORITES, "${year}_favorites")
            migrateKey(OldKeys.NOTIFICATION_SETTINGS, "${year}_notificationSettings")
            migrateKey(OldKeys.VOTES, "${year}_votes")

            // Reset onboarding
            settings.remove(Keys.ONBOARDING_COMPLETE)
        },
    ]

    private fun migrateKey(oldKey: String, newKey: String) {
        val value = settings.getStringOrNull(oldKey)
        if (value != null) {
            settings[newKey] = value
            settings.remove(oldKey)
        }
    }

    companion object {
        const val V2025 = 2025_000
        const val V2026 = 2026_000
        const val V2026_001 = 2026_001

        const val LATEST_STORAGE_VERSION: Int = V2026_001
    }

    private object Keys {
        const val STORAGE_VERSION = "storageVersion"
        const val USER_ID = "userId"
        const val ONBOARDING_COMPLETE = "onboardingComplete"
        const val THEME = "theme"
        const val FLAGS = "flags"
        const val CONFIG = "config"
    }

    /** Keys from older storage versions, used only during migrations. */
    private object OldKeys {
        // 2025_000
        const val NEWS_CACHE = "newsCache"

        // 2026_000
        const val USER_ID = "userId2025"
        const val PENDING_USER_ID = "pendingUserId2025"
        const val CONFERENCE_CACHE = "conferenceCache"
        const val CONFERENCE_INFO_CACHE = "conferenceInfoCache"
        const val FAVORITES = "favorites"
        const val NOTIFICATION_SETTINGS = "notificationSettings"
        const val VOTES = "votes"
    }
}
