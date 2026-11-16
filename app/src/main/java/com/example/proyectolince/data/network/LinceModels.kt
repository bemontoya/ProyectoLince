package com.example.proyectolince.data.network

import com.google.gson.annotations.SerializedName


data class PaginatedResponse<T>(
    val data: List<T>,
    val meta: PaginationMeta
)

data class SingleResponse<T>(
    val data: T
)

data class PaginationMeta(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)

// Modelo de Servicio
data class ServicioLinceDto(
    val id: Int,
    val codigo: String,
    val srv: String,
    val tipo: String, // TRANSFER, EXCURSION, GUIDED_SERVICE
    val fecha: String, // YYYY-MM-DD
    val horaPickup: String, // HH:mm (America/Santiago)
    val descripcion: String,
    val descripcionPadre: String?,
    val estadoServicio: String, // CF, RQ, WL, XX
    val reserva: ReservaDto?,
    val pax: PaxDto?,
    val pickup: String?,
    val dropoff: String?,
    val notas: String?,
    val oficina: OficinaDto?,
    val cliente: ClienteResumenDto?,
    val idiomaRequerido: IdiomaDto?,
    val guia: RecursoResumenDto?,
    val transportista: RecursoResumenDto?,
    val vehiculo: VehiculoDto?,
    val asignacion: AsignacionDto?
)

data class ReservaDto(
    val referencia: String?,
    val nombre: String?,
    val estado: String?
)

data class PaxDto(
    val adultos: Int,
    val ninos: Int,
    val total: Int,
    val texto: String?
)

data class OficinaDto(
    val codigo: String,
    val nombre: String
)

data class ClienteResumenDto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val pais: String?
)

data class IdiomaDto(
    val codigo: String,
    val nombre: String
)

data class RecursoResumenDto(
    val id: Int,
    val codigo: String,
    val nombre: String
)

data class VehiculoDto(
    val id: Int,
    val patente: String,
    val tipo: String,
    val capacidad: Int
)

data class AsignacionDto(
    val statusPL: String?,
    val actualizadoEn: String?
)


data class GuiaLinceDto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val oficina: OficinaDto?,
    val idiomas: List<IdiomaDto>?,
    val activo: Boolean?
)

data class TransportistaLinceDto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val oficina: OficinaDto?,
    val activo: Boolean?
)

data class ClienteLinceDto(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val pais: String?
)


data class CatalogosLinceDto(
    val oficinas: List<OficinaDto>?,
    val tiposServicio: List<CatalogoItemDto>?,
    val estadosServicio: List<CatalogoItemDto>?,
    val idiomas: List<IdiomaDto>?
)

data class CatalogoItemDto(
    val codigo: String,
    val nombre: String? = null,
    val descripcion: String? = null
)

data class ResumenLinceDto(
    val totalServicios: Int?,
    val porEstado: Map<String, Int>?,
    val porTipo: Map<String, Int>?,
    val porOficina: Map<String, Int>?
)


data class LinceErrorResponse(
    val error: LinceErrorDetail?
)

data class LinceErrorDetail(
    val code: String?,
    val message: String?,
    val requestId: String?
)