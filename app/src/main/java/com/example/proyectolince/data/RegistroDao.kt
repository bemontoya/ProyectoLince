package com.example.proyectolince.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistro(registro: RegistroOperacional)

    @Query("SELECT * FROM registros_operacionales ORDER BY fechaRegistro DESC")
    fun obtenerTodosLosRegistros(): Flow<List<RegistroOperacional>>

    @Query("SELECT * FROM registros_operacionales WHERE codigoServicio = :codigo LIMIT 1")
    suspend fun obtenerRegistroPorServicio(codigo: String): RegistroOperacional?

    @Query("SELECT * FROM registros_operacionales WHERE sincronizado = 0")
    suspend fun obtenerPendientesSincronizacion(): List<RegistroOperacional>

    @Query("UPDATE registros_operacionales SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarComoSincronizado(id: Int)
}