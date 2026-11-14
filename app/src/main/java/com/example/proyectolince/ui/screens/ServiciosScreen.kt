package com.example.proyectolince.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectolince.data.EstadoServicio
import com.example.proyectolince.data.Servicio
import com.example.proyectolince.data.TipoServicio
import com.example.proyectolince.ui.theme.*
import com.example.proyectolince.viewmodel.ServiciosViewModel
import kotlinx.coroutines.launch

private val opcionesFiltro = listOf<Pair<String, EstadoServicio?>>(
    "Todos" to null,
    "Pendientes" to EstadoServicio.PENDIENTE,
    "Confirmados" to EstadoServicio.CONFIRMADO,
    "Rechazados" to EstadoServicio.RECHAZADO
)

private data class ColoresEstado(val franja: Color, val fondoBadge: Color, val textoBadge: Color)

private fun coloresDe(estado: EstadoServicio): ColoresEstado = when (estado) {
    EstadoServicio.PENDIENTE -> ColoresEstado(NaranjaPendiente, NaranjaFondoBadge, NaranjaPendiente)
    EstadoServicio.CONFIRMADO -> ColoresEstado(VerdeConfirmado, VerdeFondoBadge, VerdeConfirmado)
    EstadoServicio.RECHAZADO -> ColoresEstado(RojoTextoRechazar, RojoRechazar, RojoTextoRechazar)
    EstadoServicio.MODIFICADO -> ColoresEstado(AzulPrincipal, AzulSecundario, AzulPrincipal)
    EstadoServicio.FINALIZADO -> ColoresEstado(TextoGris, Color(0xFFE2E8F0), TextoGris)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    nombreUsuario: String = "Driver Demo",
    rolUsuario: String = "DRIVER",
    idUsuario: String? = null,
    viewModel: ServiciosViewModel = viewModel(),
    onNavigateToDetalle: (String) -> Unit = {},
    onNavigateToHistorial: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var filtro by remember { mutableStateOf<EstadoServicio?>(null) }
    var servicioARechazar by remember { mutableStateOf<Servicio?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(rolUsuario, idUsuario) {
        viewModel.cargarServiciosPorRol(rolUsuario = rolUsuario, idUsuario = idUsuario)
    }

    val servicios = uiState.servicios
    val filtroActual = filtro
    val serviciosFiltrados =
        if (filtroActual == null) servicios
        else servicios.filter { it.estado == filtroActual }

    Scaffold(
        containerColor = FondoPantalla,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Southbound Field", fontSize = 11.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Servicios", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarServiciosPorRol(rolUsuario, idUsuario) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = TextoOscuro)
                    }
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = TextoOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoPantalla)
            )
        },
        bottomBar = {
            BarraNavegacion(
                seleccionado = Destino.SERVICIOS,
                onNavegar = { destino ->
                    when (destino) {
                        Destino.SERVICIOS -> Unit
                        Destino.DISPONIBILIDAD -> onNavigateToDisponibilidad()
                        Destino.PERFIL -> onNavigateToPerfil()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Operación en terreno", fontSize = 12.sp, color = TextoGris)
                        Text("¡Hola, $nombreUsuario!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    }
                    Surface(color = AzulSecundario, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            rolUsuario.uppercase(),
                            color = AzulPrincipal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    opcionesFiltro.forEach { (nombre, estado) ->
                        val cantidad = if (estado == null) servicios.size else servicios.count { it.estado == estado }
                        FilterChip(
                            selected = filtro == estado,
                            onClick = { filtro = estado },
                            label = { Text("$nombre $cantidad", maxLines = 1) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzulPrincipal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AzulPrincipal)
                    }
                }
            } else if (uiState.errorMessage != null) {
                item {
                    Surface(
                        color = RojoRechazar,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Error al cargar servicios",
                                fontWeight = FontWeight.Bold,
                                color = RojoTextoRechazar,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                uiState.errorMessage ?: "Error desconocido",
                                color = RojoTextoRechazar,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.cargarServiciosPorRol(rolUsuario, idUsuario) }) {
                                Text("Reintentar", color = RojoTextoRechazar, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (serviciosFiltrados.isEmpty()) {
                item {
                    Text(
                        "No hay servicios asignados para este filtro.",
                        fontSize = 13.sp,
                        color = TextoGris,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(serviciosFiltrados, key = { it.id }) { servicio ->
                    ServicioCard(
                        servicio = servicio,
                        rolUsuario = rolUsuario,
                        onClick = { onNavigateToDetalle(servicio.id) },
                        onConfirmar = {
                            viewModel.actualizarEstadoLocal(servicio.id, EstadoServicio.CONFIRMADO)
                            scope.launch { snackbarHostState.showSnackbar("Servicio confirmado") }
                        },
                        onRechazar = { servicioARechazar = servicio },
                        onCheckIn = { onNavigateToDetalle(servicio.id) }
                    )
                }
            }

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
                                Text("Contacta a Despacho central", fontSize = 11.sp, color = TextoGris)
                            }
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Phone, contentDescription = "Llamar a despacho", tint = AzulPrincipal)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    servicioARechazar?.let { servicio ->
        MotivoRechazoDialog(
            onConfirmar = { motivo ->
                viewModel.actualizarEstadoLocal(servicio.id, EstadoServicio.RECHAZADO, motivo)
                servicioARechazar = null
                scope.launch { snackbarHostState.showSnackbar("Servicio rechazado") }
            },
            onCancelar = { servicioARechazar = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServicioCard(
    servicio: Servicio,
    rolUsuario: String,
    onClick: () -> Unit,
    onConfirmar: () -> Unit,
    onRechazar: () -> Unit,
    onCheckIn: () -> Unit
) {
    val colores = coloresDe(servicio.estado)
    val esCoordinador = rolUsuario.equals("COORDINADOR", ignoreCase = true)
    val esGuia = rolUsuario.equals("GUIA", ignoreCase = true)

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(colores.franja)
            )

            Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = colores.fondoBadge,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            "• ${servicio.tipo.etiqueta} ${servicio.estado.etiqueta}",
                            color = colores.textoBadge,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${servicio.fecha} | ${servicio.hora}",
                        fontSize = 12.sp,
                        color = TextoOscuro,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val imagen = servicio.imagen
                if (imagen != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Image(
                            painter = painterResource(id = imagen),
                            contentDescription = "Foto de ${servicio.titulo}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                                    )
                                )
                        )
                        Text(
                            servicio.titulo,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Text(servicio.titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(servicio.referencia, fontSize = 12.sp, color = TextoGris)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${servicio.pasajeros} pax", fontSize = 12.sp, color = TextoGris)
                }

                if (esCoordinador) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val asignacion = buildString {
                        append("Transp: ${servicio.transportistaId ?: "Sin asignar"}")
                        if (servicio.tipo != TipoServicio.TRANSFER) {
                            append(" • Guía: ${servicio.guiaId ?: "Sin asignar"}")
                        }
                    }
                    Text(asignacion, fontSize = 11.sp, color = AzulPrincipal, fontWeight = FontWeight.SemiBold)
                }

                when (servicio.estado) {
                    EstadoServicio.PENDIENTE -> {
                        if (!esCoordinador) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onConfirmar,
                                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Confirmar", fontSize = 13.sp)
                                }
                                Button(
                                    onClick = onRechazar,
                                    colors = ButtonDefaults.buttonColors(containerColor = RojoRechazar, contentColor = RojoTextoRechazar),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rechazar", fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    EstadoServicio.CONFIRMADO -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        if (esGuia && (servicio.tipo == TipoServicio.EXCURSION || servicio.tipo == TipoServicio.GUIADO)) {
                            Button(
                                onClick = onCheckIn,
                                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.FormatListBulleted, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Iniciar check-in de pasajeros")
                            }
                        } else {
                            FilledTonalButton(
                                onClick = onClick,
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = AzulSecundario, contentColor = AzulPrincipal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Text("Ver detalles")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }

                    EstadoServicio.RECHAZADO -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Motivo: ${servicio.motivoRechazo ?: "Sin motivo"}",
                            fontSize = 12.sp,
                            color = RojoTextoRechazar
                        )
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun MotivoRechazoDialog(
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Rechazar servicio") },
        text = {
            Column {
                Text("Indica el motivo para que coordinación pueda reasignarlo.", fontSize = 13.sp, color = TextoGris)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it; error = null },
                    label = { Text("Motivo") },
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (motivo.isBlank()) error = "Ingresa el motivo del rechazo"
                else onConfirmar(motivo.trim())
            }) { Text("Rechazar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}