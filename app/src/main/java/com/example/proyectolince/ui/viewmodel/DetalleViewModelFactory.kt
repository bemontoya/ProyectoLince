package com.example.proyectolince.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectolince.data.RegistroRepository
import com.example.proyectolince.data.SessionManager

class DetalleViewModelFactory(
    private val repository: RegistroRepository,
    private val sessionManager: SessionManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetalleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetalleViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}