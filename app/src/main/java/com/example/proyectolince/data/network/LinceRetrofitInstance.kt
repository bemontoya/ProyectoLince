package com.example.proyectolince.data.network

import com.example.proyectolince.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object LinceRetrofitInstance {

    // Se "https://ws.southbound.travel/lince-api/" para la API externa
    // o "http://localhost:8080/" para backend local
    private const val BASE_URL = "https://ws.southbound.travel/lince-api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val urlPath = originalRequest.url.encodedPath

            val requestBuilder = originalRequest.newBuilder()
                .header("Accept", "application/json")

            if (!urlPath.endsWith("/health")) {
                requestBuilder.header("X-API-Key", BuildConfig.LINCE_API_KEY)
            }

            chain.proceed(requestBuilder.build())
        }
        .addInterceptor(loggingInterceptor)
        .build()

    val api: LinceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LinceApiService::class.java)
    }
}