package org.jetbrains.kotlinconf.api

import com.google.gson.*
import okhttp3.*
import okhttp3.logging.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.data.*
import retrofit2.*
import retrofit2.Call
import retrofit2.converter.gson.*
import retrofit2.http.*

interface KotlinConfApi {
    @GET("all")
    fun getAll(): Call<AllData>

    @POST("users")
    fun postUserId(@Body uuid: RequestBody): Call<ResponseBody>

    @POST("favorites")
    fun postFavorite(@Body favorite: Favorite): Call<ResponseBody>

    @HTTP(method = "DELETE", path = "favorites", hasBody = true)
    fun deleteFavorite(@Body favorite: Favorite): Call<ResponseBody>

    @POST("votes")
    fun postVote(@Body vote: Vote): Call<ResponseBody>

    @HTTP(method = "DELETE", path = "votes", hasBody = true)
    fun deleteVote(@Body vote: Vote): Call<ResponseBody>

    companion object {
        const val END_POINT = BuildConfig.API_URL
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

        val gson = GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .create()

        fun create(userId: String): KotlinConfApi {
            val client = makeOkHttpClient(userId)

            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(END_POINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(KotlinConfApi::class.java)
        }

        private fun makeOkHttpClient(userId: String) = OkHttpClient.Builder()
            .addInterceptor(makeLoggingInterceptor())
            .addInterceptor(makeHeadersInterceptor(userId))
            .build()

        private fun makeHeadersInterceptor(userId: String) = Interceptor { chain ->
            val newRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $userId")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(newRequest)
        }

        private fun makeLoggingInterceptor() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
