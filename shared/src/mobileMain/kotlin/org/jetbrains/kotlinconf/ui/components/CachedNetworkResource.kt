package org.jetbrains.kotlinconf.ui.components

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.request.get
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Resource


@OptIn(ExperimentalResourceApi::class)
class CachedNetworkResource(val url: String) : Resource {

    override suspend fun readBytes(): ByteArray = RESOURCE_CLIENT.get(url).body()

    companion object {
        private val RESOURCE_CLIENT = HttpClient {
            install(HttpCache)
        }
    }
}