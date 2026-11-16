package com.example.proyectolince.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.proyectolince.BuildConfig
import com.example.proyectolince.R
import com.example.proyectolince.data.LocationHelper
import com.example.proyectolince.data.network.WeatherRetrofitInstance
import com.example.proyectolince.ui.theme.*
import com.example.proyectolince.ui.viewmodel.DetalleViewModel
import kotlinx.coroutines.launch
import java.util.Locale

private enum class FaseServicio { SIN_INICIAR, EN_CURSO, FINALIZADO }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    servicioId: String,
    inicialUsuario: String = "D",
    rolUsuario: String = "DRIVER",
    viewModel: DetalleViewModel,
    onVolver: () -> Unit = {},
    onNavigateToServicios: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var temperatura by remember { mutableStateOf<String?>(null) }
    var condicion by remember { mutableStateOf<String?>(null) }

    val esGuia = rolUsuario.equals("GUIA", ignoreCase = true)
    val esCoordinador = rolUsuario.equals("COORDINADOR", ignoreCase = true)

    val servicio by viewModel.servicio.collectAsState()
    val errorServicio by viewModel.errorServicio.collectAsState()
    val estadoGps by viewModel.estadoGps.collectAsState()

    val codigoMostrar = servicio?.codigo ?: servicioId
    val horaMostrar = servicio?.horaPickup ?: "--:--"
    val fechaMostrar = servicio?.fecha ?: ""
    val tipoMostrar = servicio?.tipo ?: "SERVICIO"
    val tituloExcursion = servicio?.descripcion?.takeIf { it.isNotBlank() } ?: "Excursión / Tour"
    val origenMostrar = servicio?.pickup ?: "---"
    val origenRefMostrar = servicio?.notas ?: if (esGuia) "Meeting point" else "Pickup"
    val destinoMostrar = servicio?.dropoff ?: "---"
    val destinoRefMostrar = servicio?.descripcionPadre ?: "Dropoff"
    val grupoMostrar = servicio?.cliente?.nombre ?: "---"
    val paxTotal = servicio?.pax?.total ?: 0
    val contactoMostrar = servicio?.reserva?.referencia ?: "---"

    val driverNombre = servicio?.transportista?.nombre ?: "N/A"
    val driverId = servicio?.transportista?.id?.toString() ?: "N/A"
    val guiaNombre = servicio?.guia?.nombre ?: "N/A"
    val guiaId = servicio?.guia?.id?.toString() ?: "N/A"
    val staffNombre = servicio?.transportista?.nombre ?: servicio?.guia?.nombre ?: "N/A"
    val staffId = servicio?.transportista?.id?.toString() ?: servicio?.guia?.id?.toString() ?: "N/A"

    val pasajerosPresentes = remember(paxTotal) {
        mutableStateMapOf<Int, Boolean>().apply {
            for (i in 1..paxTotal) put(i, false)
        }
    }
    val totalPresentes = pasajerosPresentes.values.count { it }

    fun refrescarGps() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) {
            scope.launch {
                val snap = LocationHelper.obtenerUbicacionActual(contexto)
                viewModel.actualizarPosicionGps(snap)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            refrescarGps()
        }
    }

    LaunchedEffect(servicioId) {
        viewModel.cargarServicioYRegistro(servicioId)

        if (!esCoordinador) {
            val tienePermiso = ContextCompat.checkSelfPermission(
                contexto, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (tienePermiso) {
                refrescarGps()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    LaunchedEffect(destinoMostrar) {
        if (destinoMostrar != "---") {
            try {
                val clima = WeatherRetrofitInstance.api.obtenerClima(destinoMostrar, BuildConfig.OPENWEATHER_API_KEY)
                temperatura = "${clima.main.temp.toInt()}°C"
                condicion = clima.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
            } catch (_: Exception) {
                temperatura = "--"
                condicion = "---"
            }
        }
    }

    val registroActual by viewModel.registroActual.collectAsState()

    val fase = when {
        registroActual?.horaCheckOut != null -> FaseServicio.FINALIZADO
        registroActual?.horaCheckIn != null -> FaseServicio.EN_CURSO
        else -> FaseServicio.SIN_INICIAR
    }

    val horaCheckIn = registroActual?.horaCheckIn.orEmpty()
    val horaCheckOut = registroActual?.horaCheckOut.orEmpty()
    var observacionesText by remember { mutableStateOf("") }

    LaunchedEffect(registroActual?.observaciones) {
        observacionesText = registroActual?.observaciones.orEmpty()
    }

    fun abrirMapa() {
        val intent = Intent(Intent.ACTION_VIEW, ("geo:0,0?q=" + Uri.encode(destinoMostrar)).toUri())
        try {
            contexto.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            scope.launch { snackbarHostState.showSnackbar("Maps app not installed") }
        }
    }

    val badgeFase = when (fase) {
        FaseServicio.SIN_INICIAR -> Triple("CONFIRMED", VerdeFondoBadge, VerdeConfirmado)
        FaseServicio.EN_CURSO -> Triple("ACTIVE", AzulSecundario, AzulPrincipal)
        FaseServicio.FINALIZADO -> Triple("FINISHED", Color(0xFFE2E8F0), TextoGris)
    }

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
                        Text(
                            text = stringResource(R.string.subtitulo_app),
                            fontSize = 11.sp,
                            color = TextoGris,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when {
                                esGuia -> stringResource(R.string.titulo_detalle_excursion)
                                esCoordinador -> stringResource(R.string.titulo_supervision)
                                else -> stringResource(R.string.titulo_detalle)
                            },
                            fontSize = 20.sp,
                            color = TextoOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = TextoOscuro)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AzulPrincipal)
                            .clickable { onNavigateToPerfil() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(inicialUsuario, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoPantalla)
            )
        },
        bottomBar = {
            Column {
                Surface(color = FondoPantalla, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        if (esCoordinador) {
                            val estadoTexto = when (fase) {
                                FaseServicio.SIN_INICIAR -> "WAITING FIELD CHECK-IN"
                                FaseServicio.EN_CURSO -> "ACTIVE (CHECK-IN OK)"
                                FaseServicio.FINALIZADO -> "FINISHED"
                            }
                            val bgBarra = when (fase) {
                                FaseServicio.SIN_INICIAR -> AzulSecundario
                                FaseServicio.EN_CURSO -> VerdeFondoBadge
                                FaseServicio.FINALIZADO -> Color(0xFFE2E8F0)
                            }
                            val txtBarra = when (fase) {
                                FaseServicio.SIN_INICIAR -> AzulPrincipal
                                FaseServicio.EN_CURSO -> VerdeConfirmado
                                FaseServicio.FINALIZADO -> TextoGris
                            }
                            Surface(
                                color = bgBarra,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, tint = txtBarra, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(estadoTexto, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = txtBarra)
                                }
                            }
                        } else {
                            when (fase) {
                                FaseServicio.SIN_INICIAR -> BotonPrincipal(
                                    texto = if (esGuia) stringResource(R.string.btn_checkin_grupo) else stringResource(R.string.btn_checkin_gps),
                                    icono = if (esGuia) Icons.Default.GroupAdd else Icons.Default.HowToReg,
                                    onClick = {
                                        scope.launch {
                                            val snap = LocationHelper.obtenerUbicacionActual(contexto) ?: estadoGps.gpsActual
                                            viewModel.actualizarPosicionGps(snap)
                                            viewModel.registrarCheckInConGps(
                                                context = contexto,
                                                codigoServicio = codigoMostrar,
                                                nombreLugar = origenMostrar,
                                                horaPickupProgramada = horaMostrar,
                                                gps = snap
                                            ) { mensaje ->
                                                scope.launch { snackbarHostState.showSnackbar(mensaje) }
                                            }
                                        }
                                    }
                                )
                                FaseServicio.EN_CURSO -> BotonPrincipal(
                                    texto = if (esGuia) stringResource(R.string.btn_finalizar_tour) else stringResource(R.string.btn_checkout_gps),
                                    icono = Icons.AutoMirrored.Filled.ExitToApp,
                                    onClick = {
                                        scope.launch {
                                            val snap = LocationHelper.obtenerUbicacionActual(contexto) ?: estadoGps.gpsActual
                                            viewModel.actualizarPosicionGps(snap)
                                            val obsFinal = if (esGuia && paxTotal > 0) {
                                                val resumenPax = "Pax: $totalPresentes/$paxTotal"
                                                if (observacionesText.isBlank()) resumenPax else "$resumenPax • $observacionesText"
                                            } else {
                                                observacionesText
                                            }
                                            viewModel.registrarCheckOutConGps(
                                                context = contexto,
                                                codigoServicio = codigoMostrar,
                                                nombreLugarDestino = destinoMostrar,
                                                observaciones = obsFinal,
                                                gps = snap
                                            ) { mensaje ->
                                                scope.launch { snackbarHostState.showSnackbar(mensaje) }
                                            }
                                        }
                                    }
                                )
                                FaseServicio.FINALIZADO -> Surface(
                                    color = VerdeConfirmado,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(52.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "FINISHED",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        selected = true,
                        onClick = onNavigateToServicios,
                        icon = { Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_servicios), fontSize = 11.sp) },
                        colors = coloresNav
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToDisponibilidad,
                        icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_disponibilidad), fontSize = 11.sp) },
                        colors = coloresNav
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToPerfil,
                        icon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_perfil), fontSize = 11.sp) },
                        colors = coloresNav
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onVolver, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextoGris, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.btn_volver), fontSize = 15.sp, color = TextoGris, fontWeight = FontWeight.Medium)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(codigoMostrar, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = badgeFase.second, shape = RoundedCornerShape(16.dp)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(badgeFase.third, CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(badgeFase.first, fontSize = 11.sp, color = badgeFase.third, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            errorServicio?.let { msgError ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "API Lince: $msgError",
                            fontSize = 12.sp,
                            color = Color(0xFFB91C1C),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (!esCoordinador) {
                item {
                    val distText = estadoGps.distanciaMetros?.let { String.format(Locale.US, "%.0f m", it) } ?: "..."
                    val colorGeo = if (estadoGps.dentroDeRadio) VerdeConfirmado else Color(0xFFD97706)
                    val bgGeo = if (estadoGps.dentroDeRadio) VerdeFondoBadge else Color(0xFFFEF3C7)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EtiquetaSeccion(
                                    icono = Icons.Default.MyLocation,
                                    texto = "GEOFENCE (150M)"
                                )
                                TextButton(onClick = { refrescarGps() }, contentPadding = PaddingValues(0.dp)) {
                                    Text(stringResource(R.string.actualizar_gps), fontSize = 12.sp, color = AzulPrincipal, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = estadoGps.gpsActual?.let {
                                            String.format(Locale.US, "GPS: %.4f, %.4f (±%.0fm)", it.latitud, it.longitud, it.precisionMetros)
                                        } ?: "GPS: ...",
                                        fontSize = 12.sp,
                                        color = TextoOscuro,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Dist: $distText",
                                        fontSize = 12.sp,
                                        color = TextoGris
                                    )
                                }
                                Surface(color = bgGeo, shape = RoundedCornerShape(12.dp)) {
                                    Text(
                                        text = if (estadoGps.dentroDeRadio) stringResource(R.string.en_zona) else stringResource(R.string.fuera_radio),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorGeo,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            estadoGps.mensajeValidacion?.let { msg ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(msg, fontSize = 12.sp, color = AzulPrincipal, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

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
                            Column {
                                Text(horaMostrar, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Text(fechaMostrar, fontSize = 12.sp, color = TextoGris)
                            }
                            Surface(color = AzulSecundario, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    tipoMostrar,
                                    fontSize = 11.sp,
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (esGuia) {
                            Text(
                                text = "TOUR / ACTIVITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoGris
                            )
                            Text(
                                text = tituloExcursion,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = FondoPantalla, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = AzulPrincipal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.punto_encuentro),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris
                                    )
                                    Text(
                                        text = origenMostrar,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoOscuro
                                    )
                                    Text(
                                        text = origenRefMostrar,
                                        fontSize = 12.sp,
                                        color = TextoGris
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(top = 4.dp, end = 12.dp)
                                ) {
                                    Box(modifier = Modifier.size(12.dp).background(AzulPrincipal, CircleShape))
                                    Box(modifier = Modifier.width(2.dp).weight(1f).background(VerdeConfirmado))
                                    Box(modifier = Modifier.size(12.dp).background(VerdeConfirmado, CircleShape))
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.origen), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                    Text(origenMostrar, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Text(origenRefMostrar, fontSize = 12.sp, color = TextoGris)

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(stringResource(R.string.destino), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                    Text(destinoMostrar, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Text(destinoRefMostrar, fontSize = 12.sp, color = TextoGris)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AzulSecundario),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, tint = AzulPrincipal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Weather / $destinoMostrar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPrincipal
                            )
                            Text("${temperatura ?: "--"} · ${condicion ?: "..."}", fontSize = 13.sp, color = TextoOscuro)
                        }
                    }
                }
            }

            if (esCoordinador) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EtiquetaSeccion(Icons.Default.AssignmentInd, "STAFF")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    color = FondoPantalla,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("DRIVER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(driverNombre, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("ID: $driverId", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                                    }
                                }

                                if (!tipoMostrar.equals("TRANSFER", ignoreCase = true)) {
                                    Surface(
                                        color = FondoPantalla,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("GUIDE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(guiaNombre, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("ID: $guiaId", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = FondoPantalla, thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CLIENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                    Text(grupoMostrar, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                    Text("Ref: $contactoMostrar", fontSize = 12.sp, color = TextoGris)
                                }
                                Surface(color = AzulSecundario, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        "$paxTotal pax",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulPrincipal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                EtiquetaSeccion(Icons.Default.Group, stringResource(R.string.pasajeros))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(grupoMostrar, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(color = FondoPantalla, shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        "$paxTotal pax",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text("REF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                Text(contactoMostrar, fontSize = 12.sp, color = TextoOscuro)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                EtiquetaSeccion(
                                    icono = if (esGuia) Icons.Default.DirectionsBus else Icons.Default.DirectionsCar,
                                    texto = stringResource(R.string.recurso_asignado)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (esGuia) driverNombre else staffNombre,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Text(
                                    text = servicio?.descripcion ?: "---",
                                    fontSize = 11.sp,
                                    color = TextoGris
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = "ID",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGris
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .border(1.dp, TextoOscuro, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).background(AzulPrincipal, CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (esGuia) driverId else staffId,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoOscuro
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (esGuia && paxTotal > 0) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EtiquetaSeccion(Icons.Default.FactCheck, "PAX CHECKLIST")
                                Surface(
                                    color = if (totalPresentes == paxTotal) VerdeFondoBadge else AzulSecundario,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "$totalPresentes / $paxTotal",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (totalPresentes == paxTotal) VerdeConfirmado else AzulPrincipal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            for (indice in 1..paxTotal) {
                                val presente = pasajerosPresentes[indice] == true
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (presente) VerdeFondoBadge.copy(alpha = 0.5f) else FondoPantalla)
                                        .clickable(enabled = fase != FaseServicio.FINALIZADO) {
                                            pasajerosPresentes[indice] = !presente
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Pax #$indice — $grupoMostrar",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextoOscuro
                                        )
                                        Text(
                                            text = "Ref $contactoMostrar",
                                            fontSize = 11.sp,
                                            color = TextoGris
                                        )
                                    }
                                    Checkbox(
                                        checked = presente,
                                        enabled = fase != FaseServicio.FINALIZADO,
                                        onCheckedChange = { pasajerosPresentes[indice] = it },
                                        colors = CheckboxDefaults.colors(checkedColor = VerdeConfirmado)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (esCoordinador || fase != FaseServicio.SIN_INICIAR) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            EtiquetaSeccion(Icons.Default.Checklist, "LOG")
                            Spacer(modifier = Modifier.height(10.dp))

                            FilaRegistro(
                                "Check-in",
                                horaCheckIn.ifBlank { "---" },
                                if (horaCheckIn.isBlank()) TextoGris else TextoOscuro
                            )
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            FilaRegistro(
                                "Check-out",
                                horaCheckOut.ifBlank { "---" },
                                if (horaCheckOut.isBlank()) TextoGris else TextoOscuro
                            )

                            if (!esCoordinador || observacionesText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = observacionesText,
                                    onValueChange = { observacionesText = it },
                                    label = { Text("Notes") },
                                    readOnly = esCoordinador || fase == FaseServicio.FINALIZADO,
                                    minLines = 2,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            item {
                if (!esCoordinador && !esGuia) {
                    OutlinedButton(
                        onClick = { abrirMapa() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AzulSecundario,
                            contentColor = AzulPrincipal
                        ),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.abrir_mapa), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun BotonPrincipal(texto: String, icono: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal, contentColor = Color.White),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EtiquetaSeccion(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(texto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
    }
}

@Composable
private fun FilaRegistro(etiqueta: String, valor: String, colorValor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, fontSize = 13.sp, color = TextoGris)
        Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colorValor)
    }
}