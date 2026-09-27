package com.example.proyectolince.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen() {
    Scaffold(
        // Barra superior con el logo y notificaciones
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                    ) {
                        Text("PROYECTO LINCE", fontSize = 10.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Servicios", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = TextoOscuro)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoPantalla)
            )
        },
        // Barra de navegación inferior
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.AltRoute, contentDescription = null) },
                    label = { Text("Servicios", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulSecundario
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                    label = { Text("Detalle", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Disponibilidad", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil", fontSize = 11.sp) }
                )
            }
        },
        containerColor = FondoPantalla
    ) { paddingValores ->

        // LazyColumn habilita el SCROLL dinámico cuando el contenido sobrepasa la pantalla
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. Saludo e indicador GPS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Miércoles, 12 de Octubre", fontSize = 12.sp, color = TextoGris)
                        Text("¡Hola, Matías!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    }
                    Surface(color = VerdeFondoBadge, shape = RoundedCornerShape(16.dp)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(VerdeConfirmado, shape = RoundedCornerShape(50)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("GPS Activo", color = VerdeConfirmado, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            //Tarjetas de métricas del día
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard("HOY", "1 Servicio", modifier = Modifier.weight(1f))
                    MetricCard("SEMANA", "18 Pax", colorValor = AzulPrincipal, modifier = Modifier.weight(1f))
                    MetricCard("UNIDAD", "LNC-402", modifier = Modifier.weight(1f))
                }
            }

            // Chips para filtrar estados
            item {
                var filtroSeleccionado by remember { mutableStateOf(0) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = filtroSeleccionado == 0,
                        onClick = { filtroSeleccionado = 0 },
                        label = { Text("Todos 3") },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulPrincipal,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = filtroSeleccionado == 1,
                        onClick = { filtroSeleccionado = 1 },
                        label = { Text("Pendientes 1") },
                        shape = RoundedCornerShape(20.dp)
                    )
                    FilterChip(
                        selected = filtroSeleccionado == 2,
                        onClick = { filtroSeleccionado = 2 },
                        label = { Text("Confirmados 2") },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            //TARJETA 1: Transfer Pendiente
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = NaranjaFondoBadge, shape = RoundedCornerShape(8.dp)) {
                                Text("• TRANSFER • PENDIENTE", color = NaranjaPendiente, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("12 Oct | 08:30 AM", fontSize = 12.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Pickup Hotel Cumbres ➔ Aeropuerto El Tepual", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("✈ Vuelo LATAM LA-072 (Salida 10:45)", fontSize = 12.sp, color = TextoGris)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botones Confirmar / Rechazar
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Confirmar", fontSize = 13.sp)
                            }

                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = RojoRechazar, contentColor = RojoTextoRechazar),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rechazar", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            //Tour Confirmado
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(color = VerdeFondoBadge, shape = RoundedCornerShape(8.dp)) {
                                Text("• EXCURSIÓN • CONFIRMADO", color = VerdeConfirmado, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Text("14 Oct | 09:00 AM", fontSize = 12.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contenedor visual del Tour
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(AzulPrincipal, shape = RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text("Tour Volcán Osorno y Frutillar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Punto de Encuentro: Plaza de Armas Puerto Varas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        Text("Frente a Iglesia del Sagrado Corazón", fontSize = 11.sp, color = TextoGris)

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Check-in de Pasajeros")
                        }
                    }
                }
            }

            //Transfer AWA
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(color = AzulSecundario, shape = RoundedCornerShape(8.dp)) {
                            Text("• TRANSFER • CONFIRMADO", color = AzulPrincipal, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Drop-off Hotel AWA ➔ Centro Pto. Varas", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Servicio privado de retorno huéspedes • 2 Pax", fontSize = 12.sp, color = TextoGris)
                    }
                }
            }

            //Banner de ayuda
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = AzulPrincipal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("¿Dudas con una ruta?", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Contacta a Despacho Puerto Montt", fontSize = 10.sp, color = TextoGris)
                            }
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Phone, contentDescription = "Llamar", tint = AzulPrincipal)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// Componente pequeño reutilizable para los bloques de métricas arriba
@Composable
fun MetricCard(titulo: String, valor: String, colorValor: Color = TextoOscuro, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, fontSize = 10.sp, color = TextoGris, fontWeight = FontWeight.Bold)
            Text(valor, fontSize = 14.sp, color = colorValor, fontWeight = FontWeight.Bold)
        }
    }
}