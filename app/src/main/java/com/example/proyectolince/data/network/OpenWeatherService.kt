package com.example.proyectolince.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherData>,
    val name: String

)
data class MainData(val temp: Double)
data class WeatherData(val description: String)

interface OpenWeatherApi{
    @GET("data/2.5/weather")
    suspend fun obtenerClima(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): WeatherResponse
}

object WeatherRetrofitInstance {
    val api: OpenWeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApi::class.java)
    }
}