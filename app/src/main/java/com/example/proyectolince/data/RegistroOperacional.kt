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
    val fechaRegistro: Long = System.currentTimeMillis()
)