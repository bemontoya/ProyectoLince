package com.example.proyectolince.ui.state

import com.example.proyectolince.ui.screens.Rol

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val rolSeleccionado: Rol = Rol.DRIVER,
    val isPasswordVisible: Boolean = false,
    val mensajeError: String? = null,
    val isSuccess: Boolean = false
)

