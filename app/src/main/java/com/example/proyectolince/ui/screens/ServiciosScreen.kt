package com.example.proyectolince.ui.screens

import androidx.annotation.DrawableRes
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
import com.example.proyectolince.R
import com.example.proyectolince.ui.theme.*
import kotlinx.coroutines.launch


// Tipos de servicio : transfer, excursión o servicio guiado
enum class TipoServicio(val etiqueta: String) {
    TRANSFER("TRANSFER"),
    EXCURSION("EXCURSIÓN"),
    GUIADO("GUIADO")
}


enum class EstadoServicio(val etiqueta: String) {
    PENDIENTE("PENDIENTE"),
    CONFIRMADO("CONFIRMADO"),
    RECHAZADO("RECHAZADO"),
    MODIFICADO("MODIFICADO"),
    FINALIZADO("FINALIZADO")
}

// Una "ficha" con todos los datos de un servicio (RF02: fecha, hora, pickup, tipo y estado).
// Antes cada tarjeta tenía sus textos escritos a mano; ahora salen de esta ficha.
data class Servicio(
    val id: Int,
    val tipo: TipoServicio,
    val estado: EstadoServicio,
    val fecha: String,
    val hora: String,
    val titulo: String,
    val referencia: String,          // pickup o punto de encuentro
    val pasajeros: Int,
    val motivoRechazo: String? = null, //será null si no fue rechazado
    @DrawableRes val imagen: Int? = null
)

// datos ficticios de prueba
private val serviciosDemo = listOf(
    Servicio(
        id = 1, tipo = TipoServicio.TRANSFER, estado = EstadoServicio.PENDIENTE,
        fecha = "12 Oct", hora = "08:30",
        titulo = "Pickup Hotel Cumbres → Aeropuerto El Tepual",
        referencia = "Vuelo LATAM LA-072 (salida 10:45)", pasajeros = 4
    ),
    Servicio(
        id = 2, tipo = TipoServicio.EXCURSION, estado = EstadoServicio.CONFIRMADO,
        fecha = "14 Oct", hora = "09:00",
        titulo = "Tour Volcán Osorno y Frutillar",
        referencia = "Plaza de Armas Pto. Varas (frente a iglesia)", pasajeros = 14,
        imagen = R.drawable.volcan_osorno
    ),
    Servicio(
        id = 3, tipo = TipoServicio.TRANSFER, estado = EstadoServicio.CONFIRMADO,
        fecha = "15 Oct", hora = "16:30",
        titulo = "Drop-off Hotel AWA → Centro Pto. Varas",
        referencia = "Servicio privado de retorno", pasajeros = 2
    )
)

// texto más estado que filtran (null = mostrar todos)
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
    onNavigateToDetalle: () -> Unit = {},
    onNavigateToHistorial: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    // mutableStateListOf  si cambias un elemento, la pantalla se redibuja sola
    val servicios = remember { mutableStateListOf<Servicio>().apply { addAll(serviciosDemo) } }
    // filtro activo para que no se pierda al hacer scroll.
    var filtro by remember { mutableStateOf<EstadoServicio?>(null) }
    // servicio que el usuario quiere rechazar (si no es null, se muestra el diálogo del motivo)
    var servicioARechazar by remember { mutableStateOf<Servicio?>(null) }
    // para mostrar mensaje abajo
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filtroActual = filtro
    val serviciosFiltrados =
        if (filtroActual == null) servicios.toList()
        else servicios.filter { it.estado == filtroActual }

    // cambia el estado de un servicio
    fun actualizarServicio(id: Int, nuevoEstado: EstadoServicio, motivo: String? = null) {
        val posicion = servicios.indexOfFirst { it.id == id }
        if (posicion >= 0) {
            servicios[posicion] = servicios[posicion].copy(estado = nuevoEstado, motivoRechazo = motivo)
        }
    }

    // colores de la barra inferior
    val coloresNav = NavigationBarItemDefaults.colors(
        selectedIconColor = AzulPrincipal,
        selectedTextColor = AzulPrincipal,
        indicatorColor = AzulSecundario
    )

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
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = TextoOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoPantalla)
            )
        },

        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.AltRoute, contentDescription = null) },
                    label = { Text("Servicios", fontSize = 11.sp) },
                    colors = coloresNav
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistorial,
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Historial", fontSize = 11.sp) },
                    colors = coloresNav
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToDisponibilidad,
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Disponibilidad", fontSize = 11.sp) },
                    colors = coloresNav
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToPerfil,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil", fontSize = 11.sp) },
                    colors = coloresNav
                )
            }
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

                        Text("Miércoles, 12 de octubre • Unidad LNC-402", fontSize = 12.sp, color = TextoGris)
                        Text("¡Hola, $nombreUsuario!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                    }
                    Surface(color = AzulSecundario, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            "Modo demo",
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

            if (serviciosFiltrados.isEmpty()) {
                item {
                    Text(
                        "No hay servicios con este estado.",
                        fontSize = 13.sp,
                        color = TextoGris,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(serviciosFiltrados, key = { it.id }) { servicio ->
                ServicioCard(
                    servicio = servicio,
                    onClick = onNavigateToDetalle, // tocar la tarjeta abre el detalle
                    onConfirmar = {
                        actualizarServicio(servicio.id, EstadoServicio.CONFIRMADO)
                        scope.launch { snackbarHostState.showSnackbar("Servicio confirmado") }
                    },
                    onRechazar = { servicioARechazar = servicio },
                    onCheckIn = onNavigateToDetalle
                )
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
                        IconButton(onClick = {  }) {
                            Icon(Icons.Default.Phone, contentDescription = "Llamar a despacho", tint = AzulPrincipal)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // diálogo del motivo de rechazo, solo se muestra si servicioARechazar no es null.
    servicioARechazar?.let { servicio ->
        MotivoRechazoDialog(
            onConfirmar = { motivo ->
                actualizarServicio(servicio.id, EstadoServicio.RECHAZADO, motivo)
                servicioARechazar = null // null = cerrar el diálogo
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
    onClick: () -> Unit,
    onConfirmar: () -> Unit,
    onRechazar: () -> Unit,
    onCheckIn: () -> Unit
) {
    val colores = coloresDe(servicio.estado)

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
                            "• ${servicio.tipo.etiqueta} • ${servicio.estado.etiqueta}",
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

                when (servicio.estado) {

                    EstadoServicio.PENDIENTE -> {
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

                    EstadoServicio.CONFIRMADO -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        if (servicio.tipo == TipoServicio.EXCURSION) {
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
        onDismissRequest = onCancelar, // tocar fuera del diálogo = cancelar
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