package com.example.proyectolince.ui.viewmodel

import com.example.proyectolince.ui.screens.Rol

data class LoginUiState(
    val rolSeleccionado: Rol = Rol.DRIVER,
    val correo: String = "",
    val clave: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val mensajeError: String? = null
)