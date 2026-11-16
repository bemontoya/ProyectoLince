package com.example.proyectolince.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val email: String,
    val nombre: String,
    val rol: String,
    val linceRecursoId: Int?,
    val oficinaCodigo: String?
)

data class ConfirmacionRequest(
    val idempotencyKey: String,
    val servicioId: Int,
    val estado: String,
    val motivoRechazo: String? = null
)

data class CheckInOutRequest(
    val idempotencyKey: String,
    val servicioId: Int,
    val tipoEvento: String,
    val latitud: Double,
    val longitud: Double,
    val precisionGpsMetros: Float? = null,
    val nombreLugar: String? = null,
    val horaPickupProgramada: String? = null
)

data class DisponibilidadRequest(
    val fechaBloqueada: String,
    val franjaHoraria: String = "DIA_COMPLETO",
    val motivo: String? = null,
    val fechasConServiciosAsignados: List<String> = emptyList()
)