package com.example.proyectolince.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    inicialUsuario: String = "D",
    onNavigateToServicios: () -> Unit = {},
    onNavigateToDetalle: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {

    var chipSeleccionado by rememberSaveable { mutableStateOf(0) }
    var nuevas by rememberSaveable { mutableStateOf(3) }
    var servicioConfirmado by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var textoBusqueda by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        snackbarHostState.showSnackbar(
            message = "ALERTA PUSH: Modificación en horario de Vuelo LA-072",
            duration = SnackbarDuration.Short
        )
    }

    fun marcarLeidas() {
        nuevas = 0
        scope.launch { snackbarHostState.showSnackbar("Notificaciones marcadas como leídas") }
    }


    fun avisoSimulado() {
        scope.launch { snackbarHostState.showSnackbar("Función simulada en este MVP") }
    }


    val query = textoBusqueda.trim()

    val coincideCard1 = query.isEmpty() || "Modificación de vuelo LA-072 Tepual #SRV-001".contains(query, ignoreCase = true)
    val coincideCard2 = query.isEmpty() || "Alerta vial Ruta 225 Despacho Central Tepual".contains(query, ignoreCase = true)
    val coincideCard3 = query.isEmpty() || "Nuevo servicio asignado Transfer Hotel AWA".contains(query, ignoreCase = true)
    val coincideCard4 = query.isEmpty() || "Aviso meteorológico Vialidad Los Lagos".contains(query, ignoreCase = true)
    val coincideCard5 = query.isEmpty() || "Check-out del sistema".contains(query, ignoreCase = true)

    val mostrarCard1 = (chipSeleccionado == 0 || chipSeleccionado == 1) && coincideCard1
    val mostrarCard2 = (chipSeleccionado == 0 || chipSeleccionado == 2) && coincideCard2
    val mostrarCard3 = (chipSeleccionado == 0 || chipSeleccionado == 1) && coincideCard3
    val mostrarCard4 = (chipSeleccionado == 0 || chipSeleccionado == 2) && coincideCard4
    val mostrarCard5 = chipSeleccionado == 0 && coincideCard5


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        //  barra superior
        topBar = {
            TopAppBar(
                title = {

                    Column {
                        Text("Southbound Field", fontSize = 11.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Notificaciones", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { marcarLeidas() }) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Marcar leídas", tint = TextoOscuro)
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AzulPrincipal)
                            .clickable{onNavigateToPerfil()},
                        contentAlignment = Alignment.Center
                    ) {
                        Text(inicialUsuario, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoPantalla)
            )
        },
        // barra de navegacion inferior compartida (ninguna pestaña activa, porque Notificaciones no es una de las 3 secciones)
        bottomBar = {
            BarraNavegacion(
                seleccionado = null,
                onNavegar = { destino ->
                    when (destino) {
                        Destino.SERVICIOS -> onNavigateToServicios()
                        Destino.DISPONIBILIDAD -> onNavigateToDisponibilidad()
                        Destino.PERFIL -> onNavigateToPerfil()
                    }
                }
            )
        },
        containerColor = FondoPantalla
    ) { paddingValues ->

        // contenido desplazable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {


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
                        TextButton(onClick = { marcarLeidas() }) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = AzulPrincipal)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Marcar leídas", fontSize = 12.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        "Avisos de servicios, rutas y despacho (datos de prueba).",
                        fontSize = 12.sp,
                        color = TextoGris
                    )
                }
            }


            item {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    placeholder = { Text("Buscar alerta, vuelo o ruta...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (textoBusqueda.isNotEmpty()) {
                            IconButton(onClick = { textoBusqueda = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = chipSeleccionado == 0,
                        onClick = { chipSeleccionado = 0 },
                        label = { Text("Todas (5)", maxLines = 1) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulPrincipal,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = chipSeleccionado == 1,
                        onClick = { chipSeleccionado = 1 },
                        label = { Text("Servicios (2)", maxLines = 1) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulPrincipal,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = chipSeleccionado == 2,
                        onClick = { chipSeleccionado = 2 },
                        label = { Text("Ruta & Clima (2)", maxLines = 1) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AzulPrincipal,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }


            if (mostrarCard1 || mostrarCard2 || mostrarCard3) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("HOY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                            Spacer(modifier = Modifier.width(8.dp))

                            if (nuevas > 0) {
                                Surface(color = AzulSecundario, shape = RoundedCornerShape(12.dp)) {
                                    Text("$nuevas Nuevas", fontSize = 11.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("Actualizado recién", fontSize = 11.sp, color = TextoGris)
                    }
                }
            }


            if (mostrarCard1) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            // el borde azul marca que está sin leer; desaparece al marcar como leídas
                            .then(
                                if (nuevas > 0) Modifier.border(1.dp, AzulPrincipal, RoundedCornerShape(16.dp))
                                else Modifier
                            )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(AzulSecundario, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = AzulPrincipal)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Modificación de vuelo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).background(AzulPrincipal, CircleShape))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Servicio #SRV-001 • Hace 15 min", fontSize = 11.sp, color = TextoGris)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(color = AzulSecundario, shape = RoundedCornerShape(8.dp)) {
                                    Text("Vuelo LA-072", fontSize = 11.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "El vuelo LA-072 del grupo de prueba se retrasó 25 min. Nuevo horario estimado de salida desde pista: 11:10.",
                                fontSize = 12.sp, color = TextoOscuro
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(color = FondoPantalla, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Aeropuerto El Tepual (PMC) → Puerto Varas", fontSize = 11.sp, color = TextoGris, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onNavigateToDetalle()},
                                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.align(Alignment.End).height(48.dp)
                            ) {
                                Text("Ver Servicio")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }


            if (mostrarCard2) {
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(NaranjaFondoBadge, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = NaranjaPendiente)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Alerta vial: Ruta 225", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).background(RojoRechazar, CircleShape))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Despacho Central • Hace 45 min", fontSize = 11.sp, color = TextoGris)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
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
                                    onClick = { avisoSimulado() },
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
            }


            if (mostrarCard3) {
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(VerdeFondoBadge, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = VerdeConfirmado)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Nuevo servicio asignado", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(6.dp).background(VerdeConfirmado, CircleShape))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Asignaciones • Hace 2 horas", fontSize = 11.sp, color = TextoGris)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(color = FondoPantalla, shape = RoundedCornerShape(8.dp)) {
                                    Text("Mañana 08:30", fontSize = 11.sp, color = TextoOscuro, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Transfer Aeropuerto Tepual → Hotel AWA (3 Pax + equipaje bodega). Confirmación requerida antes de las 20:00 hrs.",
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
                                    onClick = { onNavigateToDetalle()},
                                    colors = ButtonDefaults.buttonColors(containerColor = AzulSecundario, contentColor = TextoOscuro),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Text("Ver Detalles", fontSize = 12.sp)
                                }

                                if (!servicioConfirmado) {
                                    Button(
                                        onClick = {
                                            servicioConfirmado = true
                                            scope.launch { snackbarHostState.showSnackbar("Servicio confirmado") }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = VerdeConfirmado, contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).height(48.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Confirmar", fontSize = 12.sp)
                                    }
                                } else {
                                    Surface(
                                        color = VerdeFondoBadge,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f).height(48.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VerdeConfirmado, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Confirmado", fontSize = 12.sp, color = VerdeConfirmado, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // sección de ayer y anteriores
            if (mostrarCard4 || mostrarCard5) {
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
            }


            if (mostrarCard4) {
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(AzulSecundario, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AcUnit, contentDescription = null, tint = AzulPrincipal)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Aviso meteorológico", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                        Text("Vialidad Los Lagos • Ayer, 18:20", fontSize = 11.sp, color = TextoGris)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
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
                            TextButton(onClick = { avisoSimulado() }, modifier = Modifier.align(Alignment.End)) {
                                Text("Ver reporte de vialidad", fontSize = 12.sp, color = AzulPrincipal)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp), tint = AzulPrincipal)
                            }
                        }
                    }
                }
            }


            if (mostrarCard5) {
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(VerdeFondoBadge, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = VerdeConfirmado)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Check-out completado", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                        Text("Sistema Lince • Ayer, 14:15", fontSize = 11.sp, color = TextoGris)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(color = VerdeFondoBadge, shape = RoundedCornerShape(8.dp)) {
                                    Text("Sincronizado", fontSize = 11.sp, color = VerdeConfirmado, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "La bitácora de combustible y firma digital del servicio #SRV-000 se registró en el servidor de pruebas de Lince.",
                                fontSize = 12.sp, color = TextoOscuro
                            )
                        }
                    }
                }
            }


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
                        TextButton(onClick = { avisoSimulado() }) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp), tint = AzulPrincipal)
                            Spacer(modifier = Modifier.width(4.dp))

                            Text("Verificar permisos del móvil", fontSize = 12.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}