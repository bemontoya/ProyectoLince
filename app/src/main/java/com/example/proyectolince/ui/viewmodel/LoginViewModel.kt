package com.example.proyectolince.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectolince.data.AuthRepository
import com.example.proyectolince.data.SessionManager
import com.example.proyectolince.ui.screens.Rol
import com.example.proyectolince.ui.state.LoginUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChanged(nuevoCorreo: String) {
        _uiState.update { it.copy(correo = nuevoCorreo, mensajeError = null) }
    }

    fun onClaveChanged(nuevaClave: String) {
        _uiState.update { it.copy(clave = nuevaClave, mensajeError = null) }
    }

    fun onRolSelected(nuevoRol: Rol) {
        _uiState.update { it.copy(rolSeleccionado = nuevoRol, mensajeError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(isLoading = false, mensajeError = mensaje) }
    }

    fun iniciarSesion() {
        val state = _uiState.value
        if (state.isLoading) return

        val correoTrim = state.correo.trim()

        if (correoTrim.isBlank()) {
            mostrarError("Ingresa tu correo electrónico")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correoTrim).matches()) {
            mostrarError("El formato del correo no es válido")
            return
        }
        if (state.clave.isBlank()) {
            mostrarError("Ingresa tu contraseña")
            return
        }

        _uiState.update { it.copy(isLoading = true, mensajeError = null) }

        viewModelScope.launch {
            try {
                val rolReal = authRepository.iniciarSesion(correoTrim, state.clave)

                when {
                    rolReal == null -> {
                        authRepository.cerrarSesion()
                        mostrarError("Tu usuario no tiene un rol asignado")
                    }
                    rolReal != state.rolSeleccionado -> {
                        authRepository.cerrarSesion()
                        mostrarError("Este usuario no tiene el rol ${state.rolSeleccionado.etiqueta}")
                    }
                    else -> _uiState.update {
                        it.copy(isLoading = false, isSuccess = true, mensajeError = null)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = if (e.message?.contains("401") == true) {
                    "Correo o contraseña incorrectos"
                } else {
                    "No se pudo conectar con el servidor Lince (${e.message})"
                }
                mostrarError(msg)
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}

class LoginViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(AuthRepository(sessionManager)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}