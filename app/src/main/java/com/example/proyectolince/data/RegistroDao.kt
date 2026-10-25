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
}