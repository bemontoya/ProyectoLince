package com.example.proyectolince.data.repository

import com.example.proyectolince.data.network.LinceApiService
import com.example.proyectolince.data.network.LinceRetrofitInstance
import com.example.proyectolince.data.network.ServicioLinceDto
import retrofit2.Response

sealed class ResultadoApi<out T> {
    data class Exito<T>(val datos: T) : ResultadoApi<T>()
    data class Error(val mensaje: String, val requestId: String? = null, val codigoHttp: Int? = null) : ResultadoApi<Nothing>()
}

class LinceRepository(
    private val api: LinceApiService = LinceRetrofitInstance.api
) {
    suspend fun obtenerServicios(
        page: Int = 1,
        pageSize: Int = 50,
        fecha: String? = null,
        estado: String? = null,
        tipo: String? = null
    ): ResultadoApi<List<ServicioLinceDto>> {
        return try {
            val response = api.getServicios(
                fechaDesde = fecha,
                fechaHasta = fecha,
                tipo = tipo,
                estadoServicio = estado,
                page = page,
                pageSize = pageSize.coerceAtMost(100)
            )
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
            val idNumerico = id.toIntOrNull()
                ?: return ResultadoApi.Error("ID de servicio inválido: $id")

            val response = api.getServicioById(idNumerico)
            val servicio = response.body()?.data
            if (response.isSuccessful && servicio != null) {
                ResultadoApi.Exito(servicio)
            } else {
                procesarErrorHttp(response)
            }
        } catch (e: Exception) {
            ResultadoApi.Error("Error al cargar detalle: ${e.localizedMessage}")
        }
    }

    fun validarAsignacionRecurso(servicio: ServicioLinceDto): Boolean {
        return when (servicio.tipo.uppercase()) {
            "TRANSFER", "EXCURSION" -> servicio.transportista?.id != null
            "GUIDED_SERVICE" -> servicio.guia?.id != null
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
        val reqId = response.headers()["x-request-id"]
        val msg = errorBody?.takeIf { it.isNotBlank() } ?: "Error HTTP ${response.code()}"
        return ResultadoApi.Error(
            mensaje = if (reqId != null) "$msg (RequestID: $reqId)" else msg,
            requestId = reqId,
            codigoHttp = response.code()
        )
    }
}