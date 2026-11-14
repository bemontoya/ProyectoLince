package com.example.proyectolince.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolince.data.EstadoServicio
import com.example.proyectolince.data.Servicio
import com.example.proyectolince.data.TipoServicio
import com.example.proyectolince.data.repository.LinceRepository
import com.example.proyectolince.data.repository.ResultadoApi
import com.example.proyectolince.data.toServicioUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServiciosUiState(
    val isLoading: Boolean = false,
    val servicios: List<Servicio> = emptyList(),
    val errorMessage: String? = null
)

class ServiciosViewModel(
    private val repository: LinceRepository = LinceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiciosUiState(isLoading = true))
    val uiState: StateFlow<ServiciosUiState> = _uiState.asStateFlow()

    fun cargarServiciosPorRol(rolUsuario: String, idUsuario: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val resultado = repository.obtenerServicios()) {
                is ResultadoApi.Exito -> {
                    val todos = resultado.datos.map { it.toServicioUi() }

                    val filtradosPorRol = when (rolUsuario.uppercase().trim()) {
                        "DRIVER", "TRANSPORTISTA" -> todos.filter { servicio ->
                            val esTipoDriver = servicio.tipo == TipoServicio.TRANSFER ||
                                    servicio.tipo == TipoServicio.EXCURSION
                            val coincideAsignacion = if (!idUsuario.isNullOrBlank()) {
                                servicio.transportistaId.equals(idUsuario, ignoreCase = true)
                            } else {
                                !servicio.transportistaId.isNullOrBlank()
                            }
                            esTipoDriver && coincideAsignacion
                        }
                        "GUIA" -> todos.filter { servicio ->
                            val esTipoGuia = servicio.tipo == TipoServicio.GUIADO ||
                                    servicio.tipo == TipoServicio.EXCURSION
                            val coincideAsignacion = if (!idUsuario.isNullOrBlank()) {
                                servicio.guiaId.equals(idUsuario, ignoreCase = true)
                            } else {
                                !servicio.guiaId.isNullOrBlank()
                            }
                            esTipoGuia && coincideAsignacion
                        }
                        "COORDINADOR" -> todos
                        else -> emptyList()
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

    fun actualizarEstadoLocal(idServicio: String, nuevoEstado: EstadoServicio, motivo: String? = null) {
        val listaActualizada = _uiState.value.servicios.map { servicio ->
            if (servicio.id == idServicio) {
                servicio.copy(estado = nuevoEstado, motivoRechazo = motivo)
            } else {
                servicio
            }
        }
        _uiState.value = _uiState.value.copy(servicios = listaActualizada)
    }
}