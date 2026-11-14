package com.example.proyectolince.data.repository

import com.example.proyectolince.data.network.LinceApiService
import com.example.proyectolince.data.network.LinceErrorResponse
import com.example.proyectolince.data.network.LinceRetrofitInstance
import com.example.proyectolince.data.network.ServicioLinceDto
import com.google.gson.Gson
import retrofit2.Response

sealed class ResultadoApi<out T> {
    data class Exito<T>(val datos: T) : ResultadoApi<T>()
    data class Error(val mensaje: String, val requestId: String? = null, val codigoHttp: Int? = null) : ResultadoApi<Nothing>()
}

class LinceRepository(
    private val api: LinceApiService = LinceRetrofitInstance.api
) {
    private val gson = Gson()

    suspend fun obtenerServicios(
        page: Int = 1,
        pageSize: Int = 50,
        fecha: String? = null,
        estado: String? = null,
        tipo: String? = null
    ): ResultadoApi<List<ServicioLinceDto>> {
        return try {
            val response = api.getServicios(page, pageSize.coerceAtMost(100), fecha, estado, tipo)
            if (response.isSuccessful) {
                val lista = response.body()?.data.orEmpty()
                ResultadoApi.Exito(lista)
            } else {
                procesarErrorHttp(response)
            }
        } catch (e: Exception) {
            ResultadoApi.Error("Error de conexión: ${e.localizedMessage ?: "Sin internet"}")
        }
    }

    suspend fun obtenerDetalleServicio(id: String): ResultadoApi<ServicioLinceDto> {
        return try {
            val response = api.getServicioById(id)
            if (response.isSuccessful && response.body() != null) {
                ResultadoApi.Exito(response.body()!!)
            } else {
                procesarErrorHttp(response)
            }
        } catch (e: Exception) {
            ResultadoApi.Error("Error al cargar detalle: ${e.localizedMessage}")
        }
    }

    fun validarAsignacionRecurso(servicio: ServicioLinceDto): Boolean {
        return when (servicio.tipo.uppercase()) {
            "TRANSFER", "EXCURSION" -> !servicio.transportista?.id.isNullOrBlank()
            "GUIDED_SERVICE" -> !servicio.guia?.id.isNullOrBlank()
            else -> true
        }
    }

    fun traducirEstado(codigoEstado: String): String {
        return when (codigoEstado.uppercase()) {
            "CF" -> "CONFIRMADO"
            "RQ" -> "PENDIENTE"
            "WL" -> "LISTA DE ESPERA"
            "XX" -> "CANCELADO"
            else -> codigoEstado
        }
    }

    private fun <T> procesarErrorHttp(response: Response<T>): ResultadoApi.Error {
        val errorBody = response.errorBody()?.string()
        val parsedError = try {
            gson.fromJson(errorBody, LinceErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
        val reqId = parsedError?.requestId ?: response.headers()["x-request-id"]
        val msg = parsedError?.message ?: "Error HTTP ${response.code()}"
        return ResultadoApi.Error(
            mensaje = if (reqId != null) "$msg (RequestID: $reqId)" else msg,
            requestId = reqId,
            codigoHttp = response.code()
        )
    }
}