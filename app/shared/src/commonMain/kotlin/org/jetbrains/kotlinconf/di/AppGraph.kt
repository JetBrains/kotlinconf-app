package org.jetbrains.kotlinconf.di

import androidx.lifecycle.ViewModel
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.kotlinconf.APIClient
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.FakeTimeProvider
import org.jetbrains.kotlinconf.Flags
import org.jetbrains.kotlinconf.FlagsManager
import org.jetbrains.kotlinconf.ServerBasedTimeProvider
import org.jetbrains.kotlinconf.TimeProvider
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.utils.BufferedDelegatingLogger
import org.jetbrains.kotlinconf.utils.Logger
import kotlin.reflect.KClass

interface AppGraph : ViewModelGraph {
    val conferenceService: ConferenceService
    val flagsManager: FlagsManager
    val timeProvider: TimeProvider
    val logger: Logger

    val scope: CoroutineScope
    val bufferedDelegatingLogger: BufferedDelegatingLogger
    val applicationStorage: ApplicationStorage
    val notificationConfiguration: NotificationPlatformConfiguration

    @Provides
    @SingleIn(AppScope::class)
    fun provideMetroViewModelFactory(
        viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>,
        manualAssistedFactoryProviders: Map<KClass<out ManualViewModelAssistedFactory>, Provider<ManualViewModelAssistedFactory>>,
    ): MetroViewModelFactory = object : MetroViewModelFactory() {
        override val viewModelProviders get() = viewModelProviders
        override val manualAssistedFactoryProviders get() = manualAssistedFactoryProviders
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Binds
    fun bindLogger(impl: BufferedDelegatingLogger): Logger

    @Provides
    @SingleIn(AppScope::class)
    fun provideApiClient(
        applicationStorage: ApplicationStorage,
        platformFlags: Flags,
        logger: Logger,
    ): APIClient {
        val flags = applicationStorage.getFlagsBlocking()

        val endpoint = when {
            flags != null && (flags != platformFlags) -> URLs.STAGING_URL
            else -> URLs.PRODUCTION_URL
        }
        return APIClient(endpoint, logger)
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideTimeProvider(
        applicationStorage: ApplicationStorage,
        logger: Lazy<Logger>,
        apiClient: Lazy<APIClient>,
    ): TimeProvider {
        val flags = applicationStorage.getFlagsBlocking()
        return when {
            flags != null && flags.useFakeTime -> FakeTimeProvider(logger.value)
            else -> ServerBasedTimeProvider(apiClient.value)
        }
    }
}
