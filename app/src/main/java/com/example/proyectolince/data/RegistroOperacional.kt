package com.example.proyectolince.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_operacionales")
data class RegistroOperacional(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigoServicio: String,
    val horaCheckIn: String?,
    val horaCheckOut: String?,
    val observaciones: String?,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false,
    val latitudCheckIn: Double? = null,
    val longitudCheckIn: Double? = null,
    val precisionCheckIn: Float? = null,
    val nombreLugarOrigen: String? = null,
    val horaPickupProgramada: String? = null,
    val idempotencyKeyCheckIn: String? = null,
    val latitudCheckOut: Double? = null,
    val longitudCheckOut: Double? = null,
    val precisionCheckOut: Float? = null,
    val nombreLugarDestino: String? = null,
    val idempotencyKeyCheckOut: String? = null
)