package org.jetbrains.kotlinconf.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.kotlinconf.AppConfig
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.Theme

interface ApplicationStorage {
    val userId: StateFlow<String>

    fun isOnboardingComplete(): Flow<Boolean>
    suspend fun setOnboardingComplete(value: Boolean)

    fun getTheme(): Flow<Theme>
    suspend fun setTheme(value: Theme)

    fun isExternalNavigation(): Flow<Boolean>
    suspend fun setExternalNavigation(value: Boolean)

    fun getFlagsBlocking(): Flags?
    fun getFlags(): Flow<Flags?>
    suspend fun setFlags(value: Flags)

    fun getConfig(): Flow<AppConfig?>
    suspend fun setConfig(config: AppConfig)

    fun initialize()
}
