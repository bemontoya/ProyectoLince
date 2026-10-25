package com.example.proyectolince.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.proyectolince.data.AppDatabase
import com.example.proyectolince.data.RegistroRepository
import com.example.proyectolince.ui.screens.LoginScreen
import com.example.proyectolince.ui.screens.PerfilScreen
import com.example.proyectolince.ui.screens.DetalleScreen
import com.example.proyectolince.ui.screens.NotificacionesScreen
import com.example.proyectolince.ui.screens.ServiciosScreen
import com.example.proyectolince.ui.screens.DisponibilidadScreen
import com.example.proyectolince.ui.viewmodel.DetalleViewModel
import com.example.proyectolince.ui.viewmodel.DetalleViewModelFactory

private fun NavController.navigateTab(route: String) {
    navigate(route) {
        popUpTo("servicios") { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Inicialización del repositorio de Room a través del contexto
    val contexto = LocalContext.current.applicationContext
    val database = AppDatabase.getDatabase(contexto)
    val repository = RegistroRepository(database.registroDao())

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { _ ->
                    navController.navigate("servicios") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("servicios") {
            ServiciosScreen(
                onNavigateToDetalle = { navController.navigate("detalle") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToNotificaciones = { navController.navigateTab("notificaciones") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("detalle") {
            val detalleViewModel: DetalleViewModel = viewModel(
                factory = DetalleViewModelFactory(repository)
            )

            DetalleScreen(
                viewModel = detalleViewModel,
                onVolver = { navController.popBackStack() },
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToNotificaciones = { navController.navigateTab("notificaciones") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("disponibilidad") {
            DisponibilidadScreen(
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToNotificaciones = { navController.navigateTab("notificaciones") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("notificaciones") {
            NotificacionesScreen(
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDetalle = { navController.navigate("detalle") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("perfil") {
            PerfilScreen(
                onVolver = { navController.navigateTab("servicios") },
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDetalle = { navController.navigate("detalle") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToNotificaciones = { navController.navigateTab("notificaciones") },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}