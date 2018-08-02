package org.jetbrains.kotlinconf.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.kotlinconf.data.*

internal expect val END_POINT: String

private const val PORT = 8080

class KotlinConfApi(private val userId: String) {
    private val client = HttpClient {
        install(JsonFeature) {
            // add mappers
        }
        install(ExpectSuccess)
    }

    suspend fun createUser(): Boolean {
        val response = client.call {
            url.protocol = URLProtocol.HTTP
            method = HttpMethod.Post
            url.host = END_POINT
            url.port = PORT
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
        body = favorite
    }

    suspend fun deleteFavorite(favorite: Favorite): Unit = client.request {
        method = HttpMethod.Delete
        url("favorites")
        body = favorite
    }

    suspend fun postVote(vote: Vote): Unit = client.post {
        url("votes")
        body = vote
    }

    suspend fun deleteVote(vote: Vote): Unit = client.request {
        method = HttpMethod.Delete
        url("votes")
        body = vote
    }

    private fun HttpRequestBuilder.url(path: String) {
        header("Authorization", "Bearer $userId")
        url {
            takeFrom(END_POINT)
            encodedPath = path
        }
    }
}
