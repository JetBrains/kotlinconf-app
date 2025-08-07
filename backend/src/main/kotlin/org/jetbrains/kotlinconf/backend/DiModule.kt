package org.jetbrains.kotlinconf.backend

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.backend.di.configModule
import org.jetbrains.kotlinconf.backend.di.repositoryModule
import org.jetbrains.kotlinconf.backend.di.serviceModule
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.Koin

fun Application.diModule() {
    install(Koin) {
        modules(
            serviceModule,
            repositoryModule,
            configModule,
            module {
                single<CoroutineScope> { this@diModule }
                single { createClient() } onClose { it?.close() }
                single { environment.config }
            }
        )
    }

    monitor.subscribe(ApplicationStopped) {
        it.getKoin().close()
    }
}

private fun createClient() = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true

            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
        })
    }
}
