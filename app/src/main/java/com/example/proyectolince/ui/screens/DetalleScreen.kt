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
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



// enum evita errores, no  se puede escribir una fase que no exista.
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
    vehiculo = "Mercedes Sprinter",
    capacidad = "Capacidad 12 + 1",
    patente = "KJ • 8821"
)

// devuelve la hora actual como texto
// uso SimpleDateFormat, funciona en todas las versiones de Android.
private fun horaActual(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    inicialUsuario: String = "D",
    onVolver: () -> Unit = {},
    onNavigateToServicios: () -> Unit = {},
    onNavigateToHistorial: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    val detalle = detalleDemo

    // rememberSaveable es igual que remember, pero no se pierde al girar el teléfono.
    var fase by rememberSaveable { mutableStateOf(FaseServicio.SIN_INICIAR) }
    var horaCheckIn by rememberSaveable { mutableStateOf("") }
    var horaCheckOut by rememberSaveable { mutableStateOf("") }
    var observaciones by rememberSaveable { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }   // mensajes cortos abajo
    val scope = rememberCoroutineScope()                       // para poder mostrarlos
    val contexto = LocalContext.current                        // para abrir otras app

    // Abre la app de mapas con el destino. "geo:" es un formato estándar que entienden Google Maps, Waze, etc.
    fun abrirMapa() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(detalle.destino)))
        try {
            contexto.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // avisa con un mensaje cuando no hay alguna app de mapa instalada
            scope.launch { snackbarHostState.showSnackbar("No hay una app de mapas instalada") }
        }
    }

    // el badge de arriba cambia según la fase: (texto, color de fondo, color de texto)
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
                        // el botón cambia según la fase del servicio
                        when (fase) {
                            FaseServicio.SIN_INICIAR -> BotonPrincipal(
                                texto = "REGISTRAR CHECK-IN",
                                icono = Icons.Default.HowToReg,
                                onClick = {
                                    val hora = horaActual()   // se guarda la hora exacta
                                    horaCheckIn = hora
                                    fase = FaseServicio.EN_CURSO
                                    scope.launch { snackbarHostState.showSnackbar("Check-in registrado a las $hora") }
                                }
                            )
                            FaseServicio.EN_CURSO -> BotonPrincipal(
                                texto = "REGISTRAR CHECK-OUT",
                                icono = Icons.AutoMirrored.Filled.ExitToApp,
                                onClick = {
                                    val hora = horaActual()
                                    horaCheckOut = hora
                                    fase = FaseServicio.FINALIZADO
                                    scope.launch { snackbarHostState.showSnackbar("Check-out registrado a las $hora") }
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
                        onClick = onNavigateToHistorial,
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("Historial", fontSize = 11.sp) },
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


                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 4.dp, end = 12.dp)
                            ) {
                                Box(modifier = Modifier.size(12.dp).background(AzulPrincipal, CircleShape))
                                Box(modifier = Modifier.width(2.dp).height(44.dp).background(VerdeConfirmado))
                                Box(modifier = Modifier.size(12.dp).background(VerdeConfirmado, CircleShape))
                            }
                            Column {
                                Text("ORIGEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                Text(detalle.origen, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Text(detalle.origenReferencia, fontSize = 12.sp, color = TextoGris)

                                Spacer(modifier = Modifier.height(14.dp))

                                Text("DESTINO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextoGris)
                                Text(detalle.destino, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                Text(detalle.destinoReferencia, fontSize = 12.sp, color = TextoGris)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEF2F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("${detalle.duracion} ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                                                Text("(${detalle.distancia}) • Ruta estimada", fontSize = 12.sp, color = TextoGris)
                                            }
                                            Text(detalle.ruta, fontSize = 11.sp, color = TextoGris)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Mapa simulado", fontSize = 11.sp, color = TextoGris)
                            }
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Tarjeta de pasajeros
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
                                    .clickable {
                                        scope.launch { snackbarHostState.showSnackbar("Llamada simulada (datos de prueba)") }
                                    }
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

            //registro de ejecución que solo aparece cuando ya se hizo el check-in.
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
                            // si todavía no hay check-out muestra "Pendiente" en gris
                            FilaRegistro(
                                "Check-out",
                                if (horaCheckOut.isBlank()) "Pendiente" else horaCheckOut,
                                if (horaCheckOut.isBlank()) TextoGris else TextoOscuro
                            )

                            Spacer(modifier = Modifier.height(10.dp))


                            // readOnly: cuando el servicio termina, ya no se puede editar el texto.
                            OutlinedTextField(
                                value = observaciones,
                                onValueChange = { observaciones = it },
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

// una fila del registro: nombre a la izquierda y valor a la derecha
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