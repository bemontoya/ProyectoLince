package com.example.proyectolince.data.network

import com.example.proyectolince.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object LinceRetrofitInstance {

    private const val BASE_URL = "https://ws.southbound.travel/lince-api/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val urlPath = originalRequest.url.encodedPath

            val requestBuilder = originalRequest.newBuilder()
                .header("Accept", "application/json")

            // el endpoint /health no requiere X-API-Key
            if (!urlPath.endsWith("/health")) {
                requestBuilder.header("X-API-Key", BuildConfig.LINCE_API_KEY)
            }

            chain.proceed(requestBuilder.build())
        }
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