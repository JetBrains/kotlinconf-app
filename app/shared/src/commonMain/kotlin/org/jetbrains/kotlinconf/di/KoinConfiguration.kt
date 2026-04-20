package org.jetbrains.kotlinconf.di

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
import org.jetbrains.kotlinconf.FakeTimeProvider
import org.jetbrains.kotlinconf.ServerBasedTimeProvider
import org.jetbrains.kotlinconf.TimeProvider
import org.jetbrains.kotlinconf.URLs
import org.jetbrains.kotlinconf.flags.Flags
import org.jetbrains.kotlinconf.network.ApplicationApi
import org.jetbrains.kotlinconf.storage.ApplicationStorage
import org.jetbrains.kotlinconf.utils.Logger
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton
import io.ktor.client.plugins.logging.Logger as KtorLogger

@KoinApplication
class KotlinConfKoinApp

@Module
@ComponentScan("org.jetbrains.kotlinconf")
@Configuration
class AppModule {

    @Factory
    fun defaultFlags(): Flags = Flags()

    @Singleton
    fun provideAppScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Qualifier(BaseUrl::class)
    @Singleton
    fun provideBaseUrl(
        applicationStorage: ApplicationStorage,
        platformFlags: Flags,
    ): String {
        val flags = applicationStorage.getFlagsBlocking()
        return when {
            flags != null && (flags != platformFlags) -> URLs.STAGING_URL
            else -> URLs.PRODUCTION_URL
        }
    }

    @Singleton
    fun provideHttpClient(
        applicationStorage: ApplicationStorage,
        @Qualifier(BaseUrl::class) baseUrl: String,
        logger: Logger,
    ): HttpClient {
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

    @Singleton
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