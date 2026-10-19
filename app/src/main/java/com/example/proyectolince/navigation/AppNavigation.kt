package com.example.proyectolince.navigation

//importacion para el flujo de navegacion
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//importacion de las pantallas
import com.example.proyectolince.ui.screens.LoginScreen
import com.example.proyectolince.ui.screens.PerfilScreen
import com.example.proyectolince.ui.screens.DetalleScreen
import com.example.proyectolince.ui.screens.NotificacionesScreen
import com.example.proyectolince.ui.screens.ServiciosScreen
import com.example.proyectolince.ui.screens.DisponibilidadScreen


private fun NavController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavigation(){
    // controller que va a gestionar los cambios de pantalla
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ){
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("servicios"){
                        //limpia el login del historial para no volver al presionar 'atrás'
                        popUpTo("login") {inclusive = true}
                    }
                }
            )
        }

        composable("servicios"){
            ServiciosScreen(
                onNavigateToDetalle = { navController.navigateTab("detalle") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToAvisos = { navController.navigateTab("avisos") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("detalle"){
            DetalleScreen(
                onVolver = { navController.navigateTab("servicios") },
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToAvisos = { navController.navigateTab("avisos") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("disponibilidad"){
            DisponibilidadScreen(
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDetalle = { navController.navigateTab("detalle") },
                onNavigateToAvisos = { navController.navigateTab("avisos") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("avisos"){
            NotificacionesScreen(
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDetalle = { navController.navigateTab("detalle") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("perfil"){
            PerfilScreen(
                onVolver = { navController.navigateTab("servicios") },
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDetalle = { navController.navigateTab("detalle") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToAvisos = { navController.navigateTab("avisos") },
                onCerrarSesion = {
                    navController.navigate("login"){
                        popUpTo(0) {inclusive = true}
                    }
                }
            )
        }
    }
}