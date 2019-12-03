package org.jetbrains.kotlinconf.backend

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.*

internal val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json.nonstrict)
    }
}
