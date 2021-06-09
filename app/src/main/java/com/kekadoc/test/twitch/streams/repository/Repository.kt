package com.kekadoc.test.twitch.streams.repository

import com.kekadoc.test.twitch.streams.model.TwitchTopResponse
import com.kekadoc.test.twitch.streams.model.TwitchTopResponseElement
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val TAG: String = "Repo-TAG"

interface CourseService {

    @Headers("Accept: application/vnd.twitchtv.v5+json", "Client-ID: ahuoi1tl0qmqbyi8jo8nitbmuaad7w")
    @GET("kraken/games/top")
    fun get(): Call<TwitchTopResponse>

    @Headers("Accept: application/vnd.twitchtv.v5+json", "Client-ID: ahuoi1tl0qmqbyi8jo8nitbmuaad7w")
    @GET("kraken/games/top")
    fun get(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<TwitchTopResponse>
}

object Repository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.twitch.tv/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(CourseService::class.java)

    suspend fun load(from: Int, count: Int): List<TwitchTopResponseElement> {
        return service.get(count, from).await().top
    }

}