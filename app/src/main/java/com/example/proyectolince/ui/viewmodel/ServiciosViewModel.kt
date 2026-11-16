package com.example.proyectolince.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolince.data.EstadoServicio
import com.example.proyectolince.data.Servicio
import com.example.proyectolince.data.SessionManager
import com.example.proyectolince.data.TipoServicio
import com.example.proyectolince.data.network.BackendRetrofitInstance
import com.example.proyectolince.data.network.ConfirmacionBackendRequest
import com.example.proyectolince.data.repository.LinceRepository
import com.example.proyectolince.data.repository.ResultadoApi
import com.example.proyectolince.data.toServicioUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ServiciosUiState(
    val isLoading: Boolean = false,
    val servicios: List<Servicio> = emptyList(),
    val errorMessage: String? = null
)

class ServiciosViewModel(
    private val repository: LinceRepository = LinceRepository(),
    private val sessionManager: SessionManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiciosUiState(isLoading = true))
    val uiState: StateFlow<ServiciosUiState> = _uiState.asStateFlow()

    fun cargarServiciosPorRol(rolUsuario: String, idUsuario: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val resultado = repository.obtenerServicios(page = 1, pageSize = 50)) {
                is ResultadoApi.Exito -> {
                    val todos = resultado.datos.map { it.toServicioUi() }

                    val filtradosPorRol = when (rolUsuario.uppercase().trim()) {
                        "DRIVER", "TRANSPORTISTA" -> todos.filter { servicio ->
                            servicio.tipo == TipoServicio.TRANSFER ||
                                    servicio.tipo == TipoServicio.EXCURSION
                        }
                        "GUIA" -> todos.filter { servicio ->
                            servicio.tipo == TipoServicio.GUIADO ||
                                    servicio.tipo == TipoServicio.EXCURSION
                        }
                        else -> todos
                    }

                    _uiState.value = ServiciosUiState(
                        isLoading = false,
                        servicios = filtradosPorRol
                    )
                }
                is ResultadoApi.Error -> {
                    _uiState.value = ServiciosUiState(
                        isLoading = false,
                        errorMessage = resultado.mensaje
                    )
                }
            }
        }
    }

    fun actualizarEstadoLocal(
        idServicio: String,
        nuevoEstado: EstadoServicio,
        motivo: String? = null,
        sessionOverride: SessionManager? = sessionManager
    ) {
        val listaActualizada = _uiState.value.servicios.map { servicio ->
            if (servicio.id == idServicio) {
                servicio.copy(estado = nuevoEstado, motivoRechazo = motivo)
            } else {
                servicio
            }
        }
        _uiState.value = _uiState.value.copy(servicios = listaActualizada)

        // Persistencia en microservicio Spring Boot propio (POST api/operaciones/confirmaciones)
        val manager = sessionOverride ?: return
        viewModelScope.launch {
            try {
                val bearer = manager.getBearerToken() ?: return@launch
                val estadoStr = when (nuevoEstado) {
                    EstadoServicio.CONFIRMADO -> "CONFIRMADO"
                    EstadoServicio.RECHAZADO -> "RECHAZADO"
                    else -> nuevoEstado.name
                }
                BackendRetrofitInstance.api.registrarConfirmacion(
                    bearerToken = bearer,
                    request = ConfirmacionBackendRequest(
                        idempotencyKey = "CONF-${idServicio}-${UUID.randomUUID()}",
                        servicioId = idServicio,
                        estado = estadoStr,
                        motivoRechazo = motivo
                    )
                )
            } catch (_: Exception) {
                // Si no hay conexión, el estado queda actualizado localmente
            }
        }
    }
}