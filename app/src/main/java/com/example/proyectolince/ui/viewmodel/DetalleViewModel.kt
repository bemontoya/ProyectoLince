package com.example.proyectolince.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolince.data.RegistroOperacional
import com.example.proyectolince.data.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleViewModel(private val repository: RegistroRepository) : ViewModel() {

    private val _registroActual = MutableStateFlow<RegistroOperacional?>(null)
    val registroActual: StateFlow<RegistroOperacional?> = _registroActual.asStateFlow()

    // Cargar si existe un registro previo en Room
    fun cargarRegistroServicio(codigoServicio: String) {
        viewModelScope.launch {
            val registro = repository.obtenerPorCodigo(codigoServicio)
            _registroActual.value = registro
        }
    }

    // Registrar Check-in en Room
    fun registrarCheckIn(codigoServicio: String) {
        viewModelScope.launch {
            val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val registroPrevio = repository.obtenerPorCodigo(codigoServicio)

            val nuevoRegistro = registroPrevio?.copy(horaCheckIn = horaActual)
                ?: RegistroOperacional(
                    codigoServicio = codigoServicio,
                    horaCheckIn = horaActual,
                    horaCheckOut = null,
                    observaciones = null
                )

            repository.guardarRegistro(nuevoRegistro)
            _registroActual.value = nuevoRegistro
        }
    }

    // Registrar Check-out y observaciones en Room
    fun registrarCheckOut(codigoServicio: String, observaciones: String) {
        viewModelScope.launch {
            val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val registroPrevio = repository.obtenerPorCodigo(codigoServicio)

            val nuevoRegistro = registroPrevio?.copy(
                horaCheckOut = horaActual,
                observaciones = observaciones
            ) ?: RegistroOperacional(
                codigoServicio = codigoServicio,
                horaCheckIn = null,
                horaCheckOut = horaActual,
                observaciones = observaciones
            )

            repository.guardarRegistro(nuevoRegistro)
            _registroActual.value = nuevoRegistro
        }
    }
}