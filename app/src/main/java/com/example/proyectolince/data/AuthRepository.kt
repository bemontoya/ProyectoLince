package com.example.proyectolince.data

import com.example.proyectolince.data.network.InsForgeLoginRequest
import com.example.proyectolince.data.network.InsForgeRetrofitInstance
import com.example.proyectolince.ui.screens.Rol

class AuthRepository(
    private val sessionManager: SessionManager? = null
) {

    suspend fun iniciarSesion(correo: String, clave: String): Rol? {
        val response = InsForgeRetrofitInstance.api.login(
            request = InsForgeLoginRequest(
                email = correo.trim(),
                password = clave
            )
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Credenciales inválidas o error de servidor (${response.code()})")
        }

        val data = response.body()!!
        val user = data.user
        val meta = user.userMetadata


        val rolTexto = (meta?.rol ?: user.name ?: user.email.substringBefore("@"))
            .uppercase()
            .trim()

        val rolAsignado = when {
            rolTexto.contains("COORD") -> Rol.COORDINADOR
            rolTexto.contains("GUIA") -> Rol.GUIA
            rolTexto.contains("DRIVER") || rolTexto.contains("TRANSP") -> Rol.DRIVER
            else -> return null
        }


        val recursoId = meta?.recursoId?.takeIf { it.isNotBlank() } ?: when (rolAsignado) {
            Rol.DRIVER -> "TRP-01"
            Rol.GUIA -> "GUI-01"
            Rol.COORDINADOR -> null
        }

        val nombreMostrar = meta?.nombre
            ?: user.name
            ?: rolAsignado.etiqueta


        sessionManager?.saveSession(
            token = data.accessToken,
            email = user.email,
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