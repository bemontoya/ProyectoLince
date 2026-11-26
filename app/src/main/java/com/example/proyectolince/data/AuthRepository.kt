package com.example.proyectolince.data

import com.example.proyectolince.data.network.BackendLoginRequest
import com.example.proyectolince.data.network.BackendRetrofitInstance
import com.example.proyectolince.ui.screens.Rol

class AuthRepository(
    private val sessionManager: SessionManager? = null
) {

    suspend fun iniciarSesion(correo: String, clave: String): Rol? {
        val response = BackendRetrofitInstance.api.login(
            request = BackendLoginRequest(
                email = correo.trim(),
                password = clave
            )
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Credenciales inválidas o error de servidor (${response.code()})")
        }

        val data = response.body()!!
        val rolTexto = data.rol.uppercase().trim()

        val rolAsignado = when {
            rolTexto.contains("COORD") -> Rol.COORDINADOR
            rolTexto.contains("GUIA") -> Rol.GUIA
            rolTexto.contains("DRIVER") || rolTexto.contains("TRANSP") -> Rol.DRIVER
            else -> return null
        }

        val recursoId = data.linceStaffId?.takeIf { it.isNotBlank() } ?: when (rolAsignado) {
            Rol.DRIVER -> "TRP-01"
            Rol.GUIA -> "GUI-01"
            Rol.COORDINADOR -> null
        }

        val nombreMostrar = data.nombre.ifBlank { rolAsignado.etiqueta }

        sessionManager?.saveSession(
            token = data.token,
            email = data.email,
            nombre = nombreMostrar,
            rol = rolAsignado.name,
            recursoId = recursoId
        )

        return rolAsignado
    }

    suspend fun cerrarSesion() {
        try {
            sessionManager?.clearSession()
        } catch (_: Exception) {
        }
    }
}