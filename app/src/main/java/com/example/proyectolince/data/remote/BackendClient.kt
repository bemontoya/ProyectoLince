package com.example.proyectolince.data.remote

import android.content.Context
import com.example.proyectolince.data.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BackendClient {

    private const val BASE_URL = "https://proyectolince-backend.onrender.com/"

    fun create(context: Context): LinceBackendApi {
        val sessionManager = SessionManager(context)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")

                // Evitar leer DataStore en el login donde aún no existe token
                if (!original.url.encodedPath.contains("/api/auth/login")) {
                    val bearerToken = runBlocking { sessionManager.getBearerToken() }
                    if (!bearerToken.isNullOrBlank()) {
                        requestBuilder.header("Authorization", bearerToken)
                    }
                }

                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LinceBackendApi::class.java)
    }
}