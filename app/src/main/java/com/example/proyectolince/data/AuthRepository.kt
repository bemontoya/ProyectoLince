package com.example.proyectolince.data

import com.example.proyectolince.data.network.InsforgeProvider
import com.example.proyectolince.ui.screens.Rol
import dev.insforge.auth.auth


class AuthRepository {

    private val rolesTemporales = mapOf(
        "driver01@demo.lince" to Rol.DRIVER,
        "guia01@demo.lince" to Rol.GUIA,
        "coord01@demo.lince" to Rol.COORDINADOR
    )


    suspend fun iniciarSesion(correo: String, clave: String): Rol? {
        //la linea lanza una excepcion si el correo o la contraseña está mal
        InsforgeProvider.cliente.auth.signIn(email = correo, password = clave)

        return rolesTemporales[correo.lowercase()]
    }

    // Cierra la sesión en InsForge. Se usa cuando el usuario existe pero eligió la pestaña de otro rol
    suspend fun cerrarSesion() {
        try {
            InsforgeProvider.cliente.auth.signOut()
        } catch (e: Exception) {
        }
    }
}