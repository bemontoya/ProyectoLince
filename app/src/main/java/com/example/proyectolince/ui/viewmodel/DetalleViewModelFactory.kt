package com.example.proyectolince.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectolince.data.RegistroRepository

//android no sabe como crear un viewmodel que lleve parametros por dentro, mediante esta clase se crea el viewmodel pasando la base de datos
class DetalleViewModelFactory(private val repository: RegistroRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetalleViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}