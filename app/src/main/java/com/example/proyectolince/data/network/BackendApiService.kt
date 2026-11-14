package com.example.proyectolince.data.network

import retrofit2.Response
import retrofit2.http.*

interface BackendApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: BackendLoginRequest): Response<BackendLoginResponse>

    @GET("api/geocercas")
    suspend fun getGeocercas(@Header("Authorization") bearerToken: String): Response<List<GeocercaDto>>

    @POST("api/operaciones/confirmaciones")
    suspend fun registrarConfirmacion(
        @Header("Authorization") bearerToken: String,
        @Body request: ConfirmacionBackendRequest
    ): Response<Any>

    @POST("api/operaciones/checkin-out")
    suspend fun registrarCheckInOut(
        @Header("Authorization") bearerToken: String,
        @Body request: CheckInOutBackendRequest
    ): Response<CheckInOutBackendResponse>

    @GET("api/operaciones/checkin-out/{servicioId}/ultima-posicion")
    suspend fun getUltimaPosicion(
        @Header("Authorization") bearerToken: String,
        @Path("servicioId") servicioId: String
    ): Response<CheckInOutBackendResponse>

    @GET("api/disponibilidad")
    suspend fun getDisponibilidad(
        @Header("Authorization") bearerToken: String
    ): Response<List<DisponibilidadDto>>

    @POST("api/disponibilidad")
    suspend fun bloquearFecha(
        @Header("Authorization") bearerToken: String,
        @Body request: DisponibilidadBackendRequest
    ): Response<DisponibilidadDto>

    @DELETE("api/disponibilidad/{id}")
    suspend fun eliminarBloqueo(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Long
    ): Response<Unit>
}