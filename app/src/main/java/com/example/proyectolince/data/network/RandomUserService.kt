package com.example.proyectolince.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class RandomUserResponse(val results: List<UserResult>)
data class UserResult(val picture: Picture)
data class Picture(@SerializedName("large") val fotoUrl: String)

interface RandomUserApi {
    @GET("api/")
    suspend fun obtenerUsuario(): RandomUserResponse
}

object RetrofitInstance {
    val randomUserApi: RandomUserApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://randomuser.me/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomUserApi::class.java)
    }
}