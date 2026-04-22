package org.jetbrains.kotlinconf.storage

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import org.jetbrains.kotlinconf.di.Year
import org.jetbrains.kotlinconf.di.YearScope
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scope(YearScope::class)
@Scoped
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
