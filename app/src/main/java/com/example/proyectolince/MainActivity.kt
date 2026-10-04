package com.example.proyectolince

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.proyectolince.ui.screens.ServiciosScreen
import com.example.proyectolince.ui.screens.NotificacionesScreen
import com.example.proyectolince.ui.theme.ProyectoLinceTheme
import com.example.proyectolince.ui.screens.DisponibilidadScreen
import com.example.proyectolince.ui.screens.DetalleScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoLinceTheme {
                DetalleScreen()
            }
        }
    }
}