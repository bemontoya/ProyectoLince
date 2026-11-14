package com.example.proyectolince.data.network

import com.example.proyectolince.BuildConfig
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class InsForgeLoginRequest(
    val email: String,
    val password: String
)

data class InsForgeUserMetadata(
    val nombre: String? = null,
    val rol: String? = null,
    @SerializedName("recurso_id") val recursoId: String? = null
)

data class InsForgeUser(
    val id: String,
    val email: String,
    val name: String? = null,
    @SerializedName("user_metadata") val userMetadata: InsForgeUserMetadata? = null
)

data class InsForgeLoginResponse(
    @SerializedName("access_token") val accessToken: String,
    val user: InsForgeUser
)

interface InsForgeAuthApi {
    @POST("api/auth/sessions")
    suspend fun login(
        @Header("apikey") apiKey: String = BuildConfig.INSFORGE_ANON_KEY,
        @Body request: InsForgeLoginRequest
    ): Response<InsForgeLoginResponse>
}

object InsForgeRetrofitInstance {
    val api: InsForgeAuthApi by lazy {
        val baseUrl = BuildConfig.INSFORGE_URL.trimEnd('/') + "/"
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InsForgeAuthApi::class.java)
    }
}