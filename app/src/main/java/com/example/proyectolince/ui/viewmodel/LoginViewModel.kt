package com.example.proyectolince.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolince.data.SessionManager
import com.example.proyectolince.data.remote.BackendClient
import com.example.proyectolince.data.remote.dto.LoginRequest
import com.example.proyectolince.ui.screens.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val backendApi = BackendClient.create(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onRolSelected(rol: Rol) {
        _uiState.update { it.copy(rolSeleccionado = rol, mensajeError = null) }
    }

    fun onCorreoChanged(correo: String) {
        _uiState.update { it.copy(correo = correo, mensajeError = null) }
    }

    fun onClaveChanged(clave: String) {
        _uiState.update { it.copy(clave = clave, mensajeError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun iniciarSesion() {
        val correoInput = _uiState.value.correo.trim()
        val claveInput = _uiState.value.clave.trim()

        if (correoInput.isBlank() || claveInput.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Ingresa tu correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            try {
                val response = backendApi.login(LoginRequest(correoInput, claveInput))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    val rolBackend = when (data.rol.uppercase()) {
                        "DRIVER" -> Rol.DRIVER
                        "GUIDE" -> Rol.GUIA
                        "ADMIN", "COORDINATOR" -> Rol.COORDINADOR
                        else -> _uiState.value.rolSeleccionado
                    }

                    if (rolBackend != _uiState.value.rolSeleccionado) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                mensajeError = "Tu cuenta pertenece al rol ${rolBackend.etiqueta}"
                            )
                        }
                        return@launch
                    }

                    sessionManager.saveSession(
                        token = data.token,
                        email = data.email,
                        nombre = data.nombre,
                        rol = data.rol,
                        linceRecursoId = data.linceRecursoId,
                        oficinaCodigo = data.oficinaCodigo
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            rolSeleccionado = rolBackend
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, mensajeError = "Correo o contraseña incorrectos")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensajeError = "Error de conexión: ${e.localizedMessage}"
                    )
                }
            }
        }
    }
}