package com.example.proyectolince.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectolince.data.AppDatabase
import com.example.proyectolince.data.RegistroRepository
import com.example.proyectolince.data.SessionManager
import com.example.proyectolince.ui.screens.*
import com.example.proyectolince.ui.viewmodel.*
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    val contexto = LocalContext.current.applicationContext
    val database = AppDatabase.getDatabase(contexto)
    val repository = RegistroRepository(database.registroDao())
    val sessionManager = remember { SessionManager(contexto) }

    val nombreUsuario by sessionManager.nombreFlow.collectAsState(initial = "Operador")
    val rolUsuario by sessionManager.rolFlow.collectAsState(initial = "DRIVER")
    val idUsuario by sessionManager.recursoIdFlow.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(sessionManager)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { _ ->
                    navController.navigate("servicios") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("servicios") {
            ServiciosScreen(
                nombreUsuario = nombreUsuario,
                rolUsuario = rolUsuario,
                idUsuario = idUsuario,
                onNavigateToDetalle = { servicioId ->
                    navController.navigate("detalle/$servicioId")
                },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToNotificaciones = { navController.navigateTab("notificaciones") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable(
            route = "detalle/{servicioId}",
            arguments = listOf(navArgument("servicioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val servicioId = backStackEntry.arguments?.getString("servicioId") ?: ""
            val detalleViewModel: DetalleViewModel = viewModel(
                factory = DetalleViewModelFactory(repository, sessionManager)
            )

            DetalleScreen(
                servicioId = servicioId,
                inicialUsuario = nombreUsuario.firstOrNull()?.uppercase() ?: "D",
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
                onNavigateToDetalle = { navController.navigateTab("servicios") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToPerfil = { navController.navigateTab("perfil") }
            )
        }

        composable("perfil") {
            PerfilScreen(
                onVolver = { navController.navigateTab("servicios") },
                onNavigateToServicios = { navController.navigateTab("servicios") },
                onNavigateToDetalle = { navController.navigateTab("servicios") },
                onNavigateToDisponibilidad = { navController.navigateTab("disponibilidad") },
                onNavigateToNotificaciones = { navController.navigateTab("notificaciones") },
                onCerrarSesion = {
                    scope.launch { sessionManager.clearSession() }
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}