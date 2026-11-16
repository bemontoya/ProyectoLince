package com.example.proyectolince.ui.screens

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectolince.R
import com.example.proyectolince.data.EstadoServicio
import com.example.proyectolince.data.Servicio
import com.example.proyectolince.data.TipoServicio
import com.example.proyectolince.ui.theme.*
import com.example.proyectolince.ui.viewmodel.ServiciosViewModel
import kotlinx.coroutines.launch

private data class OpcionFiltro(@StringRes val nombreRes: Int, val estado: EstadoServicio?)

private val opcionesFiltro = listOf(
    OpcionFiltro(R.string.filtro_todos, null),
    OpcionFiltro(R.string.filtro_pendientes, EstadoServicio.PENDIENTE),
    OpcionFiltro(R.string.filtro_confirmados, EstadoServicio.CONFIRMADO),
    OpcionFiltro(R.string.filtro_rechazados, EstadoServicio.RECHAZADO)
)

private data class ColoresEstado(val franja: Color, val fondoBadge: Color, val textoBadge: Color)

private fun coloresDe(estado: EstadoServicio): ColoresEstado = when (estado) {
    EstadoServicio.PENDIENTE -> ColoresEstado(NaranjaPendiente, NaranjaFondoBadge, NaranjaPendiente)
    EstadoServicio.CONFIRMADO -> ColoresEstado(VerdeConfirmado, VerdeFondoBadge, VerdeConfirmado)
    EstadoServicio.RECHAZADO -> ColoresEstado(RojoTextoRechazar, RojoRechazar, RojoTextoRechazar)
    EstadoServicio.MODIFICADO -> ColoresEstado(AzulPrincipal, AzulSecundario, AzulPrincipal)
    EstadoServicio.FINALIZADO -> ColoresEstado(TextoGris, Color(0xFFE2E8F0), TextoGris)
}

private data class DatosGuiaParseados(
    val puntoEncuentro: String,
    val codigoReserva: String?,
    val notasGrupo: String?
)

private fun parsearReferenciaGuia(referencia: String): DatosGuiaParseados {
    val partes = referencia.split("•").map { it.trim() }.filter { it.isNotBlank() }
    val rutaBruta = partes.firstOrNull().orEmpty()
    val puntos = rutaBruta.split("→").map { it.trim() }.filter { it.isNotBlank() }
    val puntoEncuentro = when {
        puntos.size >= 2 && puntos[0].equals(puntos[1], ignoreCase = true) -> puntos[0]
        puntos.isNotEmpty() -> puntos.first()
        else -> rutaBruta.ifBlank { "Por confirmar" }
    }

    val codigo = partes.firstOrNull { it.startsWith("Ref:", ignoreCase = true) }
        ?.removePrefix("Ref:")
        ?.removePrefix("ref:")
        ?.trim()

    val notas = partes.drop(1)
        .filterNot { it.startsWith("Ref:", ignoreCase = true) }
        .joinToString(" • ")
        .ifBlank { null }

    return DatosGuiaParseados(
        puntoEncuentro = puntoEncuentro,
        codigoReserva = codigo,
        notasGrupo = notas
    )
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

    val esGuia = rolUsuario.equals("GUIA", ignoreCase = true)
    val esCoordinador = rolUsuario.equals("COORDINADOR", ignoreCase = true)

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
                        Text(
                            text = stringResource(R.string.subtitulo_app),
                            fontSize = 11.sp,
                            color = TextoGris,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when {
                                esGuia -> stringResource(R.string.titulo_excursiones)
                                esCoordinador -> stringResource(R.string.titulo_control)
                                else -> stringResource(R.string.titulo_servicios)
                            },
                            fontSize = 20.sp,
                            color = TextoOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarServiciosPorRol(rolUsuario, idUsuario) }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextoOscuro)
                    }
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = TextoOscuro)
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
                        Text(
                            text = when {
                                esGuia -> stringResource(R.string.operacion_guiado)
                                esCoordinador -> stringResource(R.string.supervision_operativa)
                                else -> stringResource(R.string.operacion_terreno)
                            },
                            fontSize = 12.sp,
                            color = TextoGris
                        )
                        Text(
                            text = stringResource(R.string.saludo_hola, nombreUsuario),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
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
                    opcionesFiltro.forEach { opcion ->
                        val cantidad = if (opcion.estado == null) servicios.size else servicios.count { it.estado == opcion.estado }
                        val etiquetaFiltro = stringResource(opcion.nombreRes)
                        FilterChip(
                            selected = filtro == opcion.estado,
                            onClick = { filtro = opcion.estado },
                            label = { Text("$etiquetaFiltro $cantidad", maxLines = 1) },
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
                                "Error",
                                fontWeight = FontWeight.Bold,
                                color = RojoTextoRechazar,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                uiState.errorMessage ?: "",
                                color = RojoTextoRechazar,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.cargarServiciosPorRol(rolUsuario, idUsuario) }) {
                                Text("Retry", color = RojoTextoRechazar, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (serviciosFiltrados.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.sin_servicios),
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
                            scope.launch { snackbarHostState.showSnackbar("OK") }
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = if (esCoordinador) Icons.Default.SupportAgent else Icons.Default.Call,
                                contentDescription = null,
                                tint = AzulPrincipal
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.dudas_ruta),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Text(
                                    text = stringResource(R.string.contacta_despacho),
                                    fontSize = 11.sp,
                                    color = TextoGris
                                )
                            }
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AzulPrincipal)
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

                when {
                    esGuia -> ContenidoTarjetaGuia(servicio = servicio)
                    esCoordinador -> ContenidoTarjetaCoordinador(servicio = servicio)
                    else -> ContenidoTarjetaTransporte(servicio = servicio)
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
                                    Text(stringResource(R.string.btn_confirmar), fontSize = 13.sp)
                                }
                                Button(
                                    onClick = onRechazar,
                                    colors = ButtonDefaults.buttonColors(containerColor = RojoRechazar, contentColor = RojoTextoRechazar),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.btn_rechazar), fontSize = 13.sp)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = onClick,
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = AzulSecundario, contentColor = AzulPrincipal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text(stringResource(R.string.btn_supervisar))
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
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
                                Text(stringResource(R.string.btn_checkin_pasajeros))
                            }
                        } else {
                            FilledTonalButton(
                                onClick = onClick,
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = AzulSecundario, contentColor = AzulPrincipal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text(
                                    text = if (esCoordinador) stringResource(R.string.btn_supervisar)
                                    else stringResource(R.string.btn_ver_detalles)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }

                    EstadoServicio.RECHAZADO -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Motivo: ${servicio.motivoRechazo ?: "-"}",
                            fontSize = 12.sp,
                            color = RojoTextoRechazar,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun ContenidoTarjetaCoordinador(servicio: Servicio) {
    Text(
        text = servicio.titulo,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextoOscuro
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(servicio.referencia, fontSize = 12.sp, color = TextoGris)
    }

    Spacer(modifier = Modifier.height(4.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Groups, contentDescription = null, tint = TextoGris, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("${servicio.pasajeros} pax", fontSize = 12.sp, color = TextoGris)
    }

    Spacer(modifier = Modifier.height(10.dp))

    Surface(
        color = FondoPantalla,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tieneTransp = !servicio.transportistaId.isNullOrBlank()
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = if (tieneTransp) AzulPrincipal else NaranjaPendiente,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (tieneTransp) "Driver: #${servicio.transportistaId}" else "Driver: N/A",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (tieneTransp) TextoOscuro else NaranjaPendiente,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (servicio.tipo != TipoServicio.TRANSFER) {
                val tieneGuia = !servicio.guiaId.isNullOrBlank()
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = if (tieneGuia) AzulPrincipal else NaranjaPendiente,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (tieneGuia) "Guide: #${servicio.guiaId}" else "Guide: N/A",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (tieneGuia) TextoOscuro else NaranjaPendiente,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenidoTarjetaGuia(servicio: Servicio) {
    val datosGuia = remember(servicio.referencia) { parsearReferenciaGuia(servicio.referencia) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = servicio.titulo,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextoOscuro,
            modifier = Modifier.weight(1f)
        )

        if (datosGuia.codigoReserva != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = AzulSecundario,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Ref: ${datosGuia.codigoReserva}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrincipal,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Surface(
        color = FondoPantalla,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = AzulPrincipal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.punto_encuentro),
                        fontSize = 10.sp,
                        color = TextoGris,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = datosGuia.puntoEncuentro,
                        fontSize = 13.sp,
                        color = TextoOscuro,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = AzulPrincipal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${servicio.pasajeros} pax",
                        fontSize = 12.sp,
                        color = TextoOscuro,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (!servicio.transportistaId.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = AzulPrincipal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Driver: ${servicio.transportistaId}",
                            fontSize = 12.sp,
                            color = TextoOscuro,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (datosGuia.notasGrupo != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextoGris,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = datosGuia.notasGrupo,
                        fontSize = 11.sp,
                        color = TextoGris
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenidoTarjetaTransporte(servicio: Servicio) {
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
                contentDescription = servicio.titulo,
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
        title = { Text(stringResource(R.string.btn_rechazar)) },
        text = {
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it; error = null },
                label = { Text("Motivo / Reason") },
                isError = error != null,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (motivo.isNotBlank()) onConfirmar(motivo.trim())
            }) { Text(stringResource(R.string.btn_rechazar)) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancel") }
        }
    )
}