package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import org.jetbrains.kotlinconf.di.Year
import org.jetbrains.kotlinconf.di.YearScope

@Inject
@ContributesBinding(YearScope::class)
@SingleIn(YearScope::class)
class SettingsAssetStorage(
    @Year year: Int,
    private val settings: ObservableSettings,
) : AssetStorage {
    private val prefix = "${year}_file_"

    override suspend fun read(key: String): String? =
        settings.getStringOrNull("$prefix$key")

    override suspend fun write(key: String, content: String) {
        settings.set("$prefix$key", content)
    }
}
