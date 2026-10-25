package com.example.proyectolince

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.proyectolince.ui.theme.ProyectoLinceTheme
import com.example.proyectolince.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoLinceTheme {
                AppNavigation()
            }
        }
    }
}