package com.example.proyectolince.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*


enum class Destino(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    SERVICIOS("servicios", "Servicios", Icons.Default.AltRoute),
    DISPONIBILIDAD("disponibilidad", "Disponibilidad", Icons.Default.CalendarMonth),
    PERFIL("perfil", "Perfil", Icons.Default.Person)
}

@Composable
fun BarraNavegacion(
    seleccionado: Destino?,
    onNavegar: (Destino) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        Destino.entries.forEach { destino ->
            val esSeleccionado = seleccionado == destino

            NavigationBarItem(
                selected = esSeleccionado,
                onClick = { onNavegar(destino) },
                icon = {
                    Icon(
                        imageVector = destino.icono,
                        contentDescription = destino.etiqueta
                    )
                },
                label = {
                    Text(
                        text = destino.etiqueta,
                        fontSize = 11.sp,
                        fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzulPrincipal,
                    selectedTextColor = AzulPrincipal,
                    indicatorColor = AzulSecundario,
                    unselectedIconColor = TextoGris,
                    unselectedTextColor = TextoGris
                )
            )
        }
    }
}