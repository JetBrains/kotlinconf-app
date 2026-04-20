package org.jetbrains.kotlinconf.flags

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Singleton
class FlagsManager(
    @Provided val platformFlags: Flags,
    private val storage: ApplicationStorage,
    private val scope: CoroutineScope,
) {
    val flags: StateFlow<Flags> = storage.getFlags()
        .filterNotNull()
        .stateIn(scope, SharingStarted.Eagerly, platformFlags)

    suspend fun initAndGetFlags(): Flags {
        val storedFlags = storage.getFlags().first()
        if (storedFlags == null) {
            storage.setFlags(platformFlags)
        }
        return storedFlags ?: platformFlags
    }

    fun resetFlags() {
        scope.launch {
            storage.setFlags(platformFlags)
        }
    }

    fun updateFlags(newFlags: Flags) {
        scope.launch {
            storage.setFlags(newFlags)
        }
    }
}
