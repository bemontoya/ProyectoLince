package com.example.proyectolince.data.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BackendRetrofitInstance {

    private const val BASE_URL = "https://proyectolince-backend.onrender.com/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    val api: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }

    /**
     * Despierta el servidor en Render en segundo plano apenas abre la pantalla de Login
     * para evitar los 40-60 segundos de cold start al presionar "Iniciar sesión".
     */
    fun despertarServidorEnSegundoPlano() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(BASE_URL)
                    .head()
                    .build()
                okHttpClient.newCall(request).execute().close()
            } catch (_: Exception) {
                // Ignorar si no hay internet o aún está iniciando
            }
        }
    }
}