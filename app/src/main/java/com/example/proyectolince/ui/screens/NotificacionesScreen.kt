package com.example.proyectolince.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen() {
    Scaffold(
        //  barra superior
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
                    ) {
                        Text("PROYECTO LINCE", fontSize = 10.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Notificaciones", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Marcar leídas", tint = TextoOscuro)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar", tint = TextoOscuro)
                    }
                    // avatar simulado
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AzulPrincipal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoPantalla)
            )
        },
        // barra de navegacion inferior (destacando 'Notificaciones')
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.AltRoute, contentDescription = null) },
                    label = { Text("Servicios", fontSize = 11.sp) }
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
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    label = { Text("Disponibilidad", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("Notificaciones", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulSecundario
                    )
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
    ) { paddingValues ->

        // contenido desplazable (LazyColumn)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Header: subtítulo y botón 'Marcar leídas'
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AzulPrincipal, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "CENTRO DE ALERTAS OPERATIVAS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPrincipal
                            )
                        }
                        TextButton(onClick = { }) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = AzulPrincipal)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Marcar leídas", fontSize = 12.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        "Monitoreo en tiempo real de servicios, rutas y avisos de despacho en Chile Sur.",
                        fontSize = 12.sp,
                        color = TextoGris
                    )
                }
            }

            // chips de filtro (para clasificar contenido)
            item {
                var chipSeleccionado by remember { mutableStateOf(0) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = chipSeleccionado == 0,
                        onClick = { chipSeleccionado = 0 },
                        label = { Text("Todas (5)") },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulPrincipal,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = chipSeleccionado == 1,
                        onClick = { chipSeleccionado = 1 },
                        label = { Text("Servicios (2)") },
                        shape = RoundedCornerShape(20.dp)
                    )
                    FilterChip(
                        selected = chipSeleccionado == 2,
                        onClick = { chipSeleccionado = 2 },
                        label = { Text("Ruta & Clima (2)") },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // sección HOY
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HOY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = AzulSecundario, shape = RoundedCornerShape(12.dp)) {
                            Text("3 Nuevas", fontSize = 10.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                    }
                    Text("Actualizado recién", fontSize = 11.sp, color = TextoGris)
                }
            }

            // CARD 1: Modificación de vuelo
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AzulPrincipal, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AzulSecundario, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = AzulPrincipal)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Modificación de...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).background(AzulPrincipal, CircleShape))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Servicio #LNC-2026 • Hace 15 min", fontSize = 11.sp, color = TextoGris)
                                    }
                                }
                            }
                            Surface(color = AzulSecundario, shape = RoundedCornerShape(8.dp)) {
                                Text("Vuelo LA-072", fontSize = 11.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "El vuelo LA-072 del Grupo Silva se retrasó 25 min. Nuevo horario estimado de salida desde pista: 11:40 AM.",
                            fontSize = 12.sp, color = TextoOscuro
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(color = FondoPantalla, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Aeropuerto El Tepual (PMC) ➔ Puerto Vara...", fontSize = 11.sp, color = TextoGris, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Ver Servicio")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // CARD 2: Alerta vial
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(NaranjaFondoBadge, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = NaranjaPendiente)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Alerta vial: Ruta ...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).background(RojoRechazar, CircleShape))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Despacho Central • Hace 45 min", fontSize = 11.sp, color = TextoGris)
                                    }
                                }
                            }
                            Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp)) {
                                Text("Demora +20m", fontSize = 11.sp, color = RojoTextoRechazar, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Trabajos de mantención de carpeta asfáltica entre km 12 y 18 hacia Aeropuerto El Tepual. Se sugiere tomar desvío preventivo por Ruta Alerce.",
                            fontSize = 12.sp, color = TextoOscuro
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Traffic, contentDescription = null, tint = TextoGris, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tramo con banderero", fontSize = 11.sp, color = TextoGris)
                            }
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = AzulSecundario, contentColor = AzulPrincipal),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AltRoute, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver Ruta Alternativa", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // CARD 3: Nuevo servicio asignado
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(VerdeFondoBadge, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = VerdeConfirmado)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Nuevo servicio ...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).background(VerdeConfirmado, CircleShape))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Asignaciones Southbound • Hace 2 horas", fontSize = 11.sp, color = TextoGris)
                                    }
                                }
                            }
                            Surface(color = FondoPantalla, shape = RoundedCornerShape(8.dp)) {
                                Text("Mañana 08:30", fontSize = 11.sp, color = TextoOscuro, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Transferir en Aeropuerto Tepual ➔ Hotel AWA (3 Pax + equipaje bodega). Confirmación requerida antes de las 20:00 hrs.",
                            fontSize = 12.sp, color = TextoOscuro
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("3 Pasajeros", fontSize = 11.sp, color = TextoGris)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Luggage, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("4 Valijas", fontSize = 11.sp, color = TextoGris)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Puerto Varas", fontSize = 11.sp, color = TextoGris)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = AzulSecundario, contentColor = TextoOscuro),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Ver Detalles", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeConfirmado),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Confirmar", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // sección AYER Y ANTERIORES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AYER Y ANTERIORES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                    Text("Historial reciente", fontSize = 11.sp, color = TextoGris)
                }
            }

            // CARD 4: Aviso meteorológico
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(AzulSecundario, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AcUnit, contentDescription = null, tint = AzulPrincipal)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Aviso meteorológico...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Text("Vialidad Los Lagos • Ayer, 18:20", fontSize = 11.sp, color = TextoGris)
                                }
                            }
                            Surface(color = FondoPantalla, shape = RoundedCornerShape(8.dp)) {
                                Text("Informativo", fontSize = 11.sp, color = TextoGris, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Condición de viento leve en sector alto de la cordillera.",
                            fontSize = 12.sp, color = TextoOscuro
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { }, modifier = Modifier.align(Alignment.End)) {
                            Text("Ver reporte de vialidad", fontSize = 12.sp, color = AzulPrincipal)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp), tint = AzulPrincipal)
                        }
                    }
                }
            }

            // CARD 5: Check-out completado
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(VerdeFondoBadge, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = VerdeConfirmado)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Check-out completado.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Text("Sistema Lince • Ayer, 14:15", fontSize = 11.sp, color = TextoGris)
                                }
                            }
                            Surface(color = VerdeFondoBadge, shape = RoundedCornerShape(8.dp)) {
                                Text("Sincronizado", fontSize = 11.sp, color = VerdeConfirmado, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "La bitácora de combustible y firma digital del servicio #LNC-2024 se cargó al servidor central de Southbound.",
                            fontSize = 12.sp, color = TextoOscuro
                        )
                    }
                }
            }

            // CARD 6: Banner de Ayuda / Permisos GPS
            item {
                Surface(
                    color = AzulSecundario,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(AzulPrincipal, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CellTower, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("¿No recibes notificaciones push en ruta?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "En zonas con señal intermitente (Ruta 7, Cochamó o Petrohué) asegúrate de mantener activos los datos móviles y segundo plano.",
                            fontSize = 11.sp, color = TextoGris
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { }) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp), tint = AzulPrincipal)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verificar permisos y GPS del móvil", fontSize = 12.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}