package com.example.proyectolince.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.proyectolince.ui.screens.Rol
import com.example.proyectolince.ui.screens.UsuarioFicticio
import com.example.proyectolince.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // lista de usuarios demo
    private val usuariosDemo = listOf(
        UsuarioFicticio("driver01@demo.lince", "Lince2026", Rol.DRIVER),
        UsuarioFicticio("guia01@demo.lince", "Lince2026", Rol.GUIA),
        UsuarioFicticio("coord01@demo.lince", "Lince2026", Rol.COORDINADOR)
    )

    fun onCorreoChanged(nuevoCorreo: String) {
        _uiState.update { currentState ->
            currentState.copy(
                correo = nuevoCorreo,
                mensajeError = null
            )
        }
    }

    fun onClaveChanged(nuevaClave: String) {
        _uiState.update { currentState ->
            currentState.copy(
                clave = nuevaClave,
                mensajeError = null
            )
        }
    }

    fun onRolSelected(nuevoRol: Rol) {
        _uiState.update { currentState ->
            currentState.copy(
                rolSeleccionado = nuevoRol,
                mensajeError = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { currentState ->
            currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
        }
    }

    fun iniciarSesion() {
        val state = _uiState.value
        val correoTrim = state.correo.trim()

        // validaciones de correo y contraseña del login
        if (correoTrim.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Ingresa tu correo electrónico") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoTrim).matches()) {
            _uiState.update { it.copy(mensajeError = "El formato del correo no es válido") }
            return
        }

        if (state.clave.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Ingresa tu contraseña") }
            return
        }

        val usuario = usuariosDemo.find {
            it.correo.equals(correoTrim, ignoreCase = true) && it.clave == state.clave
        }

        if (usuario == null) {
            _uiState.update { it.copy(mensajeError = "Correo o contraseña incorrectos") }
            return
        }

        if (usuario.rol != state.rolSeleccionado) {
            _uiState.update { it.copy(mensajeError = "Este usuario no tiene el rol ${state.rolSeleccionado.etiqueta}") }
            return
        }

        // Login Exitoso
        _uiState.update { it.copy(isSuccess = true, mensajeError = null) }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}