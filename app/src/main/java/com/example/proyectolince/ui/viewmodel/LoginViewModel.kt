package com.example.proyectolince.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolince.data.AuthRepository
import com.example.proyectolince.ui.screens.Rol
import com.example.proyectolince.ui.state.LoginUiState
import dev.insforge.exceptions.InsforgeHttpException
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


    private fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(isLoading = false, mensajeError = mensaje) }
    }

    fun iniciarSesion() {
        val state = _uiState.value

        // si ya hay un intento en curso ignora el toque para evitar enviar la peticion 2 veces
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
                    // Login exitoso
                    else -> _uiState.update {
                        it.copy(isLoading = false, isSuccess = true, mensajeError = null)
                    }
                }
            } catch (e: InsforgeHttpException) {
                val mensaje = when (e.error) {
                    "INVALID_CREDENTIALS", "USER_NOT_FOUND" -> "Correo o contraseña incorrectos"
                    "EMAIL_NOT_VERIFIED" -> "Debes verificar tu correo antes de ingresar"
                    "INVALID_EMAIL" -> "El formato del correo no es válido"
                    else -> "No se pudo iniciar sesión: ${e.message}"
                }
                mostrarError(mensaje)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mostrarError("No se pudo conectar. Revisa tu conexión a internet.")
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}