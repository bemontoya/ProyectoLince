package com.example.proyectolince.data.network

data class BackendLoginRequest(
    val email: String,
    val password: String
)

data class BackendLoginResponse(
    val token: String,
    val email: String,
    val nombre: String,
    val rol: String,          // DRIVER, GUIA, COORDINADOR
    val linceStaffId: String?
)

data class ConfirmacionBackendRequest(
    val idempotencyKey: String,
    val servicioId: String,
    val estado: String,       // CONFIRMADO o RECHAZADO
    val motivoRechazo: String? = null
)

data class CheckInOutBackendRequest(
    val idempotencyKey: String,
    val servicioId: String,
    val tipoEvento: String,   // CHECK_IN o CHECK_OUT
    val latitud: Double,
    val longitud: Double,
    val precisionGpsMetros: Float? = null,
    val nombreLugar: String? = null,
    val horaPickupProgramada: String? = null
)

data class CheckInOutBackendResponse(
    val id: Long,
    val servicioId: String,
    val tipoEvento: String,
    val distanciaGeocercaMetros: Double?,
    val validado: Boolean,
    val observacionValidacion: String?,
    val fechaHoraRegistro: String
)

data class GeocercaDto(
    val id: Long,
    val nombreLugar: String,
    val latitud: Double,
    val longitud: Double,
    val radioMetros: Double
)

data class DisponibilidadBackendRequest(
    val fechaBloqueada: String, // YYYY-MM-DD
    val franjaHoraria: String = "DIA_COMPLETO",
    val motivo: String? = null,
    val fechasConServiciosAsignados: List<String> = emptyList()
)

data class DisponibilidadDto(
    val id: Long,
    val usuarioEmail: String,
    val fechaBloqueada: String,
    val franjaHoraria: String?,
    val motivo: String?
)