package com.example.proyectolince // Revisa el nombre de tu paquete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.proyectolince.ui.screens.ServiciosScreen
import com.example.proyectolince.screens.NotificacionesScreen
import com.example.proyectolince.ui.theme.ProyectoLinceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoLinceTheme {
                // Se llama directamente a la pantalla Screen
                NotificacionesScreen()
            }
        }
    }
}