package com.example.proyectolince.data.network

// envoltorio para respuestas paginadas de la API de Lince
data class LincePagedResponse<T>(
    val data: List<T>,
    val meta: LinceMeta?
)

data class LinceMeta(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)

// error estándar de la API con requestId para trazabilidad
data class LinceErrorResponse(
    val error: String?,
    val message: String?,
    val requestId: String?
)

data class ServicioLinceDto(
    val id: String,
    val codigo: String?,               // Ej: "SRV-000245"
    val srv: String?,                  // TR, GS, EX
    val tipo: String,                  // TRANSFER, GUIDED_SERVICE, EXCURSION
    val fecha: String,                 // YYYY-MM-DD
    val horaPickup: String?,           // HH:mm (America/Santiago)
    val descripcion: String?,
    val descripcionPadre: String?,     // Tour al que pertenece
    val estadoServicio: String?,       // CF, RQ, WL, XX
    val pickup: String?,
    val dropoff: String?,
    val notas: String?,                // Ej: "Pickup en lobby del hotel"
    val pax: PaxLinceDto?,
    val asignacion: AsignacionLinceDto?,
    val reserva: ReservaLinceDto?,
    val cliente: ClienteRefDto?,
    val guia: StaffRefDto?,
    val transportista: StaffRefDto?
)

data class PaxLinceDto(
    val texto: String?,                // Ej: "2+1" (adultos+niños)
    val adultos: Int?,
    val ninos: Int?,
    val total: Int?
)

data class AsignacionLinceDto(
    val statusPL: String?              // CF, RQ, DL, CX o null
)

data class ReservaLinceDto(
    val referencia: String?,
    val estado: String?                // CF, RQ, WL, XX
)

data class ClienteRefDto(
    val id: String?,
    val nombre: String?
)

data class StaffRefDto(
    val id: String?,
    val nombre: String?
)

data class GuiaLinceDto(
    val id: String,
    val nombre: String,
    val idiomas: List<String>?,
    val telefono: String?,
    val activo: Boolean?
)

data class TransportistaLinceDto(
    val id: String,
    val nombre: String,
    val vehiculo: String?,
    val patente: String?,
    val capacidad: Int?,
    val telefono: String?
)

data class ClienteLinceDto(
    val id: String,
    val nombre: String,
    val telefono: String?,
    val email: String?
)

data class CatalogosLinceDto(
    val tiposServicio: List<String>?,
    val estados: Map<String, String>?
)

data class ResumenLinceDto(
    val totalServicios: Int?,
    val confirmados: Int?,
    val pendientes: Int?
)