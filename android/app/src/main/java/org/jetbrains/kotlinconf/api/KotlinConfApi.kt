package org.jetbrains.kotlinconf.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import org.jetbrains.kotlinconf.data.AllData
import org.jetbrains.kotlinconf.data.Favorite
import org.jetbrains.kotlinconf.data.Vote
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.kotlinconf.BuildConfig
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

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
                    .baseUrl(KotlinConfApi.END_POINT)
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
