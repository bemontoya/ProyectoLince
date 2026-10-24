package com.example.proyectolince.ui.screens


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*

// lista de las secciones que aparecen en la barra de abajo.
// para agregar o quitar una pestaña, se hace aqui y cambia en todas las pantallas a la vez.
enum class Destino(val etiqueta: String, val icono: ImageVector) {
    SERVICIOS("Servicios", Icons.Default.AltRoute),

    DISPONIBILIDAD("Disponibilidad", Icons.Default.CalendarToday),
    PERFIL("Perfil", Icons.Default.Person)
}

// una sola barra para toda la app
@Composable
fun BarraNavegacion(
    seleccionado: Destino,
    onNavegar: (Destino) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        // values() devuelve todas las opciones del enum: dibuja una pestaña por cada una
        Destino.values().forEach { destino ->
            NavigationBarItem(
                selected = destino == seleccionado,
                onClick = { onNavegar(destino) },
                icon = { Icon(destino.icono, contentDescription = null) },
                label = { Text(destino.etiqueta, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzulPrincipal,
                    selectedTextColor = AzulPrincipal,
                    indicatorColor = AzulSecundario
                )
            )
        }
    }
}