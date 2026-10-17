package com.example.proyectolince.navigation

//importacion para el flujo de navegacion
import androidx.compose.runtime.Composable
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
                onNavigateToDetalle = {navController.navigate("detalle")},
                onNavigateToDisponibilidad = {navController.navigate("disponibilidad")},
                onNavigateToAvisos = {navController.navigate("avisos")},
                onNavigateToPerfil = {navController.navigate("perfil")}


            )
        }

        composable("detalle"){
            DetalleScreen(
                onVolver = {navController.popBackStack()},
                onNavigateToPerfil = {navController.navigate("perfil")},
                onNavigateToServicios = {navController.navigate("servicios")},
                onNavigateToDisponibilidad = {navController.navigate("disponibilidad")},
                onNavigateToAvisos = {navController.navigate("avisos")}

            )
        }

        composable("disponibilidad"){
            DisponibilidadScreen(
                onNavigateToServicios = {navController.navigate("servicios")},
                onNavigateToDetalle = {navController.navigate("detalle")},
                onNavigateToAvisos = {navController.navigate("avisos")},
                onNavigateToPerfil = {navController.navigate("perfil")}
            )
        }

        composable("avisos"){
            NotificacionesScreen(
                onNavigateToServicios = {navController.navigate("servicios")},
                onNavigateToDetalle = {navController.navigate("detalle")},
                onNavigateToDisponibilidad = {navController.navigate("disponibilidad")},
                onNavigateToPerfil = {navController.navigate("perfil")}
            )
        }

        composable("perfil"){
            PerfilScreen(
                onVolver = {navController.popBackStack()},
                onCerrarSesion = {
                    navController.navigate("login"){
                        popUpTo(0) {inclusive = true}
                    }
                }
            )
        }
    }
}
