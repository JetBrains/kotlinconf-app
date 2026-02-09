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
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.kotlinconf.network.ApplicationApi
import io.ktor.client.plugins.logging.Logger as KtorLogger
import org.jetbrains.kotlinconf.ConferenceService
import org.jetbrains.kotlinconf.FakeGoldenKodeeService
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
    val goldenKodeeService: FakeGoldenKodeeService
    val flagsManager: FlagsManager
    val timeProvider: TimeProvider
    val logger: Logger

    val scope: CoroutineScope
    val bufferedDelegatingLogger: BufferedDelegatingLogger
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
    fun provideHttpClient(
        applicationStorage: ApplicationStorage,
        platformFlags: Flags,
        logger: Logger,
    ): HttpClient {

        val flags = applicationStorage.getFlagsBlocking()
        val baseUrl = when {
            flags != null && (flags != platformFlags) -> URLs.STAGING_URL
            else -> URLs.PRODUCTION_URL
        }

        return HttpClient {
            install(ContentNegotiation) {
                json()
            }

            install(Logging) {
                level = LogLevel.HEADERS
                this.logger = object : KtorLogger {
                    override fun log(message: String) {
                        logger.log("HttpClient") { message }
                    }
                }
            }

            expectSuccess = true
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            install(DefaultRequest) {
                url.takeFrom(baseUrl)
            }
        }
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideTimeProvider(
        applicationStorage: ApplicationStorage,
        logger: Lazy<Logger>,
        applicationApi: Lazy<ApplicationApi>,
    ): TimeProvider {
        val flags = applicationStorage.getFlagsBlocking()
        return when {
            flags != null && flags.useFakeTime -> FakeTimeProvider(logger.value)
            else -> ServerBasedTimeProvider(applicationApi.value)
        }
    }
}
