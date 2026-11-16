package com.example.proyectolince.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LinceApiService {

    @GET("api/v1/servicios")
    suspend fun getServicios(
        @Query("fechaDesde") fechaDesde: String? = null,
        @Query("fechaHasta") fechaHasta: String? = null,
        @Query("oficina") oficina: String? = null,
        @Query("tipo") tipo: String? = null,
        @Query("estadoServicio") estadoServicio: String? = null,
        @Query("estadoReserva") estadoReserva: String? = null,
        @Query("statusPL") statusPL: String? = null,
        @Query("clienteId") clienteId: Int? = null,
        @Query("guiaId") guiaId: Int? = null,
        @Query("transportistaId") transportistaId: Int? = null,
        @Query("q") query: String? = null,
        @Query("orden") orden: String = "fecha",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<PaginatedResponse<ServicioLinceDto>>

    @GET("api/v1/servicios/{id}")
    suspend fun getServicioById(
        @Path("id") id: Int
    ): Response<SingleResponse<ServicioLinceDto>>

    @GET("api/v1/guias")
    suspend fun getGuias(
        @Query("oficina") oficina: String? = null,
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<PaginatedResponse<GuiaLinceDto>>

    @GET("api/v1/transportistas")
    suspend fun getTransportistas(
        @Query("oficina") oficina: String? = null,
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<PaginatedResponse<TransportistaLinceDto>>

    @GET("api/v1/clientes")
    suspend fun getClientes(
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<PaginatedResponse<ClienteLinceDto>>

    @GET("api/v1/catalogos")
    suspend fun getCatalogos(): Response<SingleResponse<CatalogosLinceDto>>

    @GET("api/v1/resumen")
    suspend fun getResumen(
        @Query("fechaDesde") fechaDesde: String? = null,
        @Query("fechaHasta") fechaHasta: String? = null,
        @Query("oficina") oficina: String? = null
    ): Response<SingleResponse<ResumenLinceDto>>
}