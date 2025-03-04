package org.jetbrains.kotlinconf.backend

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinconf.backend.di.configModule
import org.jetbrains.kotlinconf.backend.di.repositoryModule
import org.jetbrains.kotlinconf.backend.di.serviceModule
import org.koin.dsl.module
import org.koin.ktor.ext.getKoin
import org.koin.ktor.plugin.Koin

fun Application.diModule() {
    val client = HttpClient {
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

    install(Koin) {
        modules(
            serviceModule,
            repositoryModule,
            configModule,
            module {
                single { client }
                single { environment.config }
            }
        )
    }

    monitor.subscribe(ApplicationStopping) {
        val closeableServices = it.getKoin().getAll<Closeable>()
        closeableServices.forEach { it.close() }
    }
}