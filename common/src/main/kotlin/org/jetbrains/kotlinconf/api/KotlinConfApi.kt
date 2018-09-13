package org.jetbrains.kotlinconf.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*

internal expect val END_POINT: String

class KotlinConfApi(private val userId: String) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                setMapper(AllData::class, AllData.serializer())
                setMapper(Favorite::class, Favorite.serializer())
                setMapper(Vote::class, Vote.serializer())
            }
        }
        install(ExpectSuccess)
    }

    suspend fun createUser(): Boolean {
        val response = client.call {
            url(urlString = END_POINT)
            method = HttpMethod.Post
            url.encodedPath = "users"
            body = userId
        }.response

        response.close()
        return response.status.isSuccess()
    }

    suspend fun getAll(): AllData = client.get {
        url("all")
    }

    suspend fun postFavorite(favorite: Favorite): Unit = client.post {
        url("favorites")
        json()
        body = favorite
    }

    suspend fun deleteFavorite(favorite: Favorite): Unit = client.delete {
        url("favorites")
        json()
        body = favorite
    }

    suspend fun postVote(vote: Vote): Unit = client.post {
        url("votes")
        json()
        body = vote
    }

    suspend fun deleteVote(vote: Vote): Unit = client.delete {
        url("votes")
        json()
        body = vote
    }

    private fun HttpRequestBuilder.json() {
        contentType(ContentType.Application.Json)
    }

    private fun HttpRequestBuilder.url(path: String) {
        header(HttpHeaders.Authorization, "Bearer $userId")
        header(HttpHeaders.CacheControl, "no-cache")
        url {
            takeFrom(END_POINT)
            encodedPath = path
        }
    }
}
