package com.example.proyectolince.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*
import com.example.proyectolince.ui.viewmodel.DetalleViewModel
import kotlinx.coroutines.launch
import com.example.proyectolince.data.network.WeatherRetrofitInstance
import com.example.proyectolince.BuildConfig


private enum class FaseServicio { SIN_INICIAR, EN_CURSO, FINALIZADO }

private data class DetalleServicio(
    val codigo: String,
    val fecha: String,
    val hora: String,
    val tipo: String,
    val origen: String,
    val origenReferencia: String,
    val destino: String,
    val destinoReferencia: String,
    val duracion: String,
    val distancia: String,
    val ruta: String,
    val pasajeros: Int,
    val grupo: String,
    val contacto: String,
    val telefonoContacto: String,
    val vehiculo: String,
    val capacidad: String,
    val patente: String
)

private val detalleDemo = DetalleServicio(
    codigo = "#SRV-001",
    fecha = "Miércoles, 12 de octubre",
    hora = "08:30",
    tipo = "TRANSFER PRIVADO",
    origen = "Hotel Cumbres Puerto Varas",
    origenReferencia = "Lobby principal",
    destino = "Aeropuerto El Tepual (Puerto Montt)",
    destinoReferencia = "Terminal salidas nacionales",
    duracion = "35 min",
    distancia = "28 km",
    ruta = "Vía Ruta 225 y V-505",
    pasajeros = 4,
    grupo = "Grupo de prueba",
    contacto = "Titular demo",
    telefonoContacto = "+56912345678",
    vehiculo = "Mercedes Sprinter",
    capacidad = "Capacidad 12 + 1",
    patente = "KJ • 8821"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    inicialUsuario: String = "D",
    viewModel: DetalleViewModel,
    onVolver: () -> Unit = {},
    onNavigateToServicios: () -> Unit = {},
    onNavigateToHistorial: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    val detalle = detalleDemo
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var temperatura by remember { mutableStateOf<String?>(null) }
    var condicion by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val clima = WeatherRetrofitInstance.api.obtenerClima("Puerto Montt", BuildConfig.OPENWEATHER_API_KEY)
            temperatura = "${clima.main.temp.toInt()}°C"
            condicion = clima.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
        } catch (e: Exception){
            temperatura = "14°C"
            condicion = "Parcialmente nublado (Offline)"
        }
    }

    // Cargar registro de Room al abrir la pantalla
    LaunchedEffect(detalle.codigo) {
        viewModel.cargarRegistroServicio(detalle.codigo)
    }

    val registroActual by viewModel.registroActual.collectAsState()

    // Determinar la fase actual basándose en los datos de Room
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

    // Recurso Nativo 1: Mapas externos
    fun abrirMapa() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(detalle.destino)))
        try {
            contexto.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            scope.launch { snackbarHostState.showSnackbar("No hay una app de mapas instalada") }
        }
    }

    // Recurso Nativo 2: Llamada telefónica mediante Intent ACTION_DIAL
    fun realizarLlamada() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${detalle.telefonoContacto}"))
        try {
            contexto.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            scope.launch { snackbarHostState.showSnackbar("No se puede realizar la llamada") }
        }
    }

    val badgeFase = when (fase) {
        FaseServicio.SIN_INICIAR -> Triple("CONFIRMADO", VerdeFondoBadge, VerdeConfirmado)
        FaseServicio.EN_CURSO -> Triple("EN CURSO", AzulSecundario, AzulPrincipal)
        FaseServicio.FINALIZADO -> Triple("FINALIZADO", Color(0xFFE2E8F0), TextoGris)
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
                        Text("Southbound Field", fontSize = 11.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Detalle", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notificaciones", tint = TextoOscuro)
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
                        when (fase) {
                            FaseServicio.SIN_INICIAR -> BotonPrincipal(
                                texto = "REGISTRAR CHECK-IN",
                                icono = Icons.Default.HowToReg,
                                onClick = {
                                    viewModel.registrarCheckIn(detalle.codigo)
                                    scope.launch { snackbarHostState.showSnackbar("Check-in guardado en Room") }
                                }
                            )
                            FaseServicio.EN_CURSO -> BotonPrincipal(
                                texto = "REGISTRAR CHECK-OUT",
                                icono = Icons.AutoMirrored.Filled.ExitToApp,
                                onClick = {
                                    viewModel.registrarCheckOut(detalle.codigo, observacionesText)
                                    scope.launch { snackbarHostState.showSnackbar("Check-out guardado en Room") }
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
                                    Text("SERVICIO FINALIZADO", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        selected = true,
                        onClick = onNavigateToServicios,
                        icon = { Icon(Icons.Default.AltRoute, contentDescription = null) },
                        label = { Text("Servicios", fontSize = 11.sp) },
                        colors = coloresNav
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToDisponibilidad,
                        icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        label = { Text("Disponibilidad", fontSize = 11.sp) },
                        colors = coloresNav
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToPerfil,
                        icon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
                        label = { Text("Perfil", fontSize = 11.sp) },
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TextoGris, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Volver", fontSize = 15.sp, color = TextoGris, fontWeight = FontWeight.Medium)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(detalle.codigo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoGris)
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
                                Text(detalle.hora, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Text(detalle.fecha, fontSize = 12.sp, color = TextoGris)
                            }
                            Surface(color = AzulSecundario, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    detalle.tipo,
                                    fontSize = 11.sp,
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 4.dp, end = 12.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(AzulPrincipal, CircleShape)
                                )


                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .weight(1f) // Ocupa todo el espacio vertical entre el punto superior e inferior
                                        .background(VerdeConfirmado)
                                )


                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(VerdeConfirmado, CircleShape)
                                )
                            }


                            Column(modifier = Modifier.weight(1f)) {
                                Text("ORIGEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                Text(detalle.origen, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Text(detalle.origenReferencia, fontSize = 12.sp, color = TextoGris)

                                Spacer(modifier = Modifier.height(16.dp))

                                Text("DESTINO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                Text(detalle.destino, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Text(detalle.destinoReferencia, fontSize = 12.sp, color = TextoGris)
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

                ){
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(Icons.Default.WbSunny, contentDescription = null, tint = AzulPrincipal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column{
                            Text("Clima en ruta / Aeropuerto Tepual", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                            Text("${temperatura ?: "--"} • ${condicion ?: "Cargando..."}", fontSize = 13.sp, color = TextoOscuro )
                        }

                    }
                }

            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            EtiquetaSeccion(Icons.Default.Group, "PASAJEROS")
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(detalle.grupo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(color = FondoPantalla, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    "${detalle.pasajeros} pax",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGris,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("CONTACTO TITULAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                            Text(detalle.contacto, fontSize = 12.sp, color = TextoOscuro)

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = VerdeFondoBadge,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { realizarLlamada() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = VerdeConfirmado, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Llamar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VerdeConfirmado)
                                }
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            EtiquetaSeccion(Icons.Default.DirectionsCar, "VEHÍCULO")
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(detalle.vehiculo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                            Text(detalle.capacidad, fontSize = 11.sp, color = TextoGris)

                            Spacer(modifier = Modifier.height(18.dp))

                            Text("PATENTE ASIGNADA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .border(1.dp, TextoOscuro, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).background(AzulPrincipal, CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(detalle.patente, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                }
                            }
                        }
                    }
                }
            }

            if (fase != FaseServicio.SIN_INICIAR) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            EtiquetaSeccion(Icons.Default.Checklist, "REGISTRO DE EJECUCIÓN")
                            Spacer(modifier = Modifier.height(10.dp))

                            FilaRegistro("Check-in", horaCheckIn, TextoOscuro)
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            FilaRegistro(
                                "Check-out",
                                if (horaCheckOut.isBlank()) "Pendiente" else horaCheckOut,
                                if (horaCheckOut.isBlank()) TextoGris else TextoOscuro
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = observacionesText,
                                onValueChange = { observacionesText = it },
                                label = { Text("Observaciones (opcional)") },
                                readOnly = fase == FaseServicio.FINALIZADO,
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
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
                    Text("Abrir en Waze / Google Maps", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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