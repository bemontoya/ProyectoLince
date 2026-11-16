package com.example.proyectolince.data.network

import com.example.proyectolince.BuildConfig
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object LinceClient {

    private const val BASE_URL = "https://ws.southbound.travel/lince-api/"
    private val gson = Gson()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-API-Key", BuildConfig.LINCE_API_KEY)
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .build()

    val api: LinceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(LinceApiService::class.java)
    }

    fun <T> parseErrorMessage(response: Response<T>): String {
        val headerRequestId = response.headers()["X-Request-Id"]
        return try {
            val rawError = response.errorBody()?.string()
            val parsed = gson.fromJson(rawError, LinceErrorResponse::class.java)?.error
            val code = parsed?.code ?: "HTTP_${response.code()}"
            val message = parsed?.message ?: response.message()
            val reqId = parsed?.requestId ?: headerRequestId ?: "N/A"
            "[$code] $message (Request ID: $reqId)"
        } catch (_: Exception) {
            "Error HTTP ${response.code()} (Request ID: ${headerRequestId ?: "N/A"})"
        }
    }
}