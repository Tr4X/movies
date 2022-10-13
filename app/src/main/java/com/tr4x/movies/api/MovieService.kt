package com.tr4x.movies.api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Github API communication setup via Retrofit.
 */
interface MovieService {
    /**
     * Get repos ordered by stars.
     */

    @GET("movies.json")
    suspend fun getMovies(): MovieResponse

    companion object {
        //private const val BASE_URL = "https://www.digitalia.be/coding_test/"
        private const val BASE_URL = "http://hometardis.ddns.net:1313/"
        //private const val BASE_URL = "http://192.168.1.6:1313/"

        fun create(): MovieService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MovieService::class.java)
        }
    }
}
