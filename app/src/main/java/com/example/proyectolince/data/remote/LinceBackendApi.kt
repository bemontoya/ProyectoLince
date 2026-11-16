package com.example.proyectolince.data.remote

import com.example.proyectolince.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LinceBackendApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/operaciones/confirmaciones")
    suspend fun confirmarServicio(@Body request: ConfirmacionRequest): Response<Any>

    @GET("api/operaciones/confirmaciones")
    suspend fun listarConfirmaciones(): Response<List<Any>>

    @POST("api/operaciones/checkin-out")
    suspend fun registrarCheckInOut(@Body request: CheckInOutRequest): Response<Any>

    @GET("api/operaciones/checkin-out/{servicioId}/ultima-posicion")
    suspend fun obtenerUltimaPosicion(@Path("servicioId") servicioId: Int): Response<Any>

    @POST("api/disponibilidad")
    suspend fun bloquearDisponibilidad(@Body request: DisponibilidadRequest): Response<Any>

    @GET("api/disponibilidad")
    suspend fun listarDisponibilidad(): Response<List<Any>>
}