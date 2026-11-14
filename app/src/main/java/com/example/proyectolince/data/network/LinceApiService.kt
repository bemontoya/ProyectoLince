package com.example.proyectolince.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LinceApiService {

    @GET("health")
    suspend fun checkHealth(): Response<Any>

    @GET("api/v1/servicios")
    suspend fun getServicios(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50,
        @Query("fecha") fecha: String? = null,
        @Query("estado") estado: String? = null,
        @Query("tipo") tipo: String? = null
    ): Response<LincePagedResponse<ServicioLinceDto>>

    @GET("api/v1/servicios/{id}")
    suspend fun getServicioById(@Path("id") id: String): Response<ServicioLinceDto>

    @GET("api/v1/guias")
    suspend fun getGuias(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<LincePagedResponse<GuiaLinceDto>>

    @GET("api/v1/guias/{id}")
    suspend fun getGuiaById(@Path("id") id: String): Response<GuiaLinceDto>

    @GET("api/v1/transportistas")
    suspend fun getTransportistas(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<LincePagedResponse<TransportistaLinceDto>>

    @GET("api/v1/transportistas/{id}")
    suspend fun getTransportistaById(@Path("id") id: String): Response<TransportistaLinceDto>

    @GET("api/v1/clientes")
    suspend fun getClientes(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<LincePagedResponse<ClienteLinceDto>>

    @GET("api/v1/clientes/{id}")
    suspend fun getClienteById(@Path("id") id: String): Response<ClienteLinceDto>

    @GET("api/v1/catalogos")
    suspend fun getCatalogos(): Response<CatalogosLinceDto>

    @GET("api/v1/resumen")
    suspend fun getResumen(): Response<ResumenLinceDto>
}