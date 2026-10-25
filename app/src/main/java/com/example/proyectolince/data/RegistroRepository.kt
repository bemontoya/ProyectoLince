package com.example.proyectolince.data

import kotlinx.coroutines.flow.Flow

class RegistroRepository(private val registroDao: RegistroDao) {

    val todosLosRegistros: Flow<List<RegistroOperacional>> = registroDao.obtenerTodosLosRegistros()

    suspend fun guardarRegistro(registro: RegistroOperacional) {
        registroDao.insertarRegistro(registro)
    }

    suspend fun obtenerPorCodigo(codigo: String): RegistroOperacional? {
        return registroDao.obtenerRegistroPorServicio(codigo)
    }
}