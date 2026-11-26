package com.example.proyectolince.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.ConcurrentHashMap

data class WeatherResponse(
    val main: MainData,
    val weather: List<WeatherData>,
    val name: String
)
data class MainData(val temp: Double)
data class WeatherData(val description: String)

data class OpenMeteoResponse(
    @SerializedName("current")
    val current: OpenMeteoCurrent?
)

data class OpenMeteoCurrent(
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("weather_code")
    val weatherCode: Int
)

data class OpenMeteoGeoResponse(
    @SerializedName("results")
    val results: List<OpenMeteoGeoResult>?
)

data class OpenMeteoGeoResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("country_code")
    val countryCode: String?
)

interface OpenMeteoService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code",
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse
}

interface OpenMeteoGeocodingService {
    @GET("v1/search")
    suspend fun searchLocation(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "es",
        @Query("format") format: String = "json"
    ): OpenMeteoGeoResponse
}

class WeatherApiAdapter(
    private val service: OpenMeteoService,
    private val geocodingService: OpenMeteoGeocodingService
) {
    private val coordsCache = ConcurrentHashMap<String, Triple<Double, Double, String>>()

    suspend fun obtenerClima(
        ciudad: String,
        apiKey: String = "",
        unidades: String = "metric",
        idioma: String = "es"
    ): WeatherResponse {
        val (lat, lon, nombreLimpio) = resolverCoordenadas(ciudad)
        val response = service.getForecast(latitude = lat, longitude = lon)
        val temp = response.current?.temperature ?: 20.0
        val code = response.current?.weatherCode ?: 0
        val esIngles = idioma.startsWith("en", ignoreCase = true)

        return WeatherResponse(
            main = MainData(temp = temp),
            weather = listOf(WeatherData(description = traducirCodigoClima(code, esIngles))),
            name = nombreLimpio
        )
    }

    private suspend fun resolverCoordenadas(ciudad: String): Triple<Double, Double, String> {
        val key = ciudad.trim().lowercase()
        coordsCache[key]?.let { return it }

        val conocido = when {
            key.contains("pucon") || key.contains("pucón") || key.contains("villarrica") -> Triple(-39.2822, -71.9543, "Pucón")
            key.contains("varas") || key.contains("osorno") || key.contains("petrohu") -> Triple(-41.3195, -72.9854, "Puerto Varas")
            key.contains("puerto montt") -> Triple(-41.4693, -72.9424, "Puerto Montt")
            key.contains("natales") || key.contains("torres") || key.contains("paine") -> Triple(-51.7299, -72.5060, "Puerto Natales")
            key.contains("atacama") || key.contains("calama") || key.contains("tatio") || key.contains("luna") -> Triple(-22.9087, -68.1997, "San Pedro de Atacama")
            key.contains("valparaiso") || key.contains("valparaíso") || key.contains("viña") || key.contains("reñaca") -> Triple(-33.0472, -71.6127, "Valparaíso")
            key.contains("aeropuerto") || key.contains("scl") || key.contains("pudahuel") -> Triple(-33.3930, -70.7858, "Aeropuerto SCL")
            key.contains("maipo") || key.contains("embalse") || key.contains("yeso") -> Triple(-33.6400, -70.3500, "Cajón del Maipo")
            key.contains("nevado") || key.contains("farellones") || key.contains("colorado") -> Triple(-33.3547, -70.2494, "Valle Nevado")
            key.contains("colchagua") || key.contains("santa cruz") -> Triple(-34.6386, -71.3658, "Santa Cruz")
            key.contains("casablanca") -> Triple(-33.3206, -71.4072, "Casablanca")
            key.contains("serena") || key.contains("coquimbo") || key.contains("elqui") -> Triple(-29.9027, -71.2519, "La Serena")
            key.contains("chiloé") || key.contains("chiloe") || key.contains("castro") -> Triple(-42.4824, -73.7643, "Castro, Chiloé")
            key.contains("iquique") -> Triple(-20.2307, -70.1357, "Iquique")
            key.contains("arica") -> Triple(-18.4783, -70.3126, "Arica")
            key.contains("punta arenas") -> Triple(-53.1638, -70.9171, "Punta Arenas")
            key.contains("temuco") -> Triple(-38.7359, -72.5904, "Temuco")
            key.contains("concepcion") || key.contains("concepción") -> Triple(-36.8201, -73.0444, "Concepción")
            key.contains("valdivia") -> Triple(-39.8142, -73.2459, "Valdivia")
            else -> null
        }
        if (conocido != null) {
            coordsCache[key] = conocido
            return conocido
        }

        val base = ciudad.substringBefore(",").substringBefore("-").substringBefore("(").trim()
        val sinPrefijoHotel = base.replace(
            Regex("^(hotel|hostal|resort|viña|vina|tour|excursión|excursion|termas|parque)\\s+", RegexOption.IGNORE_CASE),
            ""
        ).trim()

        val candidatos = listOfNotNull(
            ciudad.substringAfter(",", "").trim().takeIf { it.length >= 3 },
            base.takeIf { it.length >= 3 },
            sinPrefijoHotel.takeIf { it.length >= 3 }
        ).distinct()

        for (termino in candidatos) {
            try {
                val geoResp = geocodingService.searchLocation(name = termino)
                val match = geoResp.results?.firstOrNull { it.countryCode.equals("CL", ignoreCase = true) }
                    ?: geoResp.results?.firstOrNull()
                if (match != null) {
                    val encontrado = Triple(match.latitude, match.longitude, match.name)
                    coordsCache[key] = encontrado
                    return encontrado
                }
            } catch (_: Exception) {
            }
        }

        return Triple(-33.4489, -70.6693, base.ifBlank { "Santiago" })
    }

    private fun traducirCodigoClima(code: Int, isEnglish: Boolean): String = when (code) {
        0 -> if (isEnglish) "Clear sky" else "Despejado"
        1, 2, 3 -> if (isEnglish) "Partly cloudy" else "Parcialmente nublado"
        45, 48 -> if (isEnglish) "Fog" else "Neblina"
        51, 53, 55, 61, 63, 65 -> if (isEnglish) "Rain" else "Lluvia"
        71, 73, 75 -> if (isEnglish) "Snow" else "Nieve"
        80, 81, 82 -> if (isEnglish) "Rain showers" else "Chubascos"
        95, 96, 99 -> if (isEnglish) "Thunderstorm" else "Tormenta eléctrica"
        else -> if (isEnglish) "Clear" else "Despejado"
    }
}

object WeatherRetrofitInstance {
    private val openMeteoService: OpenMeteoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoService::class.java)
    }

    private val openMeteoGeocodingService: OpenMeteoGeocodingService by lazy {
        Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoGeocodingService::class.java)
    }

    val api: WeatherApiAdapter by lazy {
        WeatherApiAdapter(openMeteoService, openMeteoGeocodingService)
    }
}