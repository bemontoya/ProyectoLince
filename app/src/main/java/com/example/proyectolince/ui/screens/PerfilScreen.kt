package com.example.proyectolince.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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

// importación de la paleta de colores definida en ui.theme
import com.example.proyectolince.ui.theme.*

import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.proyectolince.data.network.RetrofitInstance
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    nombreUsuario: String = "Driver Demo",
    inicialUsuario: String = "D",
    onVolver: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNavigateToServicios: () -> Unit = {},
    onNavigateToDetalle: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {}
) {

    var fotoUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try{
            val respuesta = RetrofitInstance.randomUserApi.obtenerUsuario()
            fotoUrl = respuesta.results.firstOrNull()?.picture?.fotoUrl
        } catch (e: Exception) {
            //si falla o no hay conexion se mantiene null para mostrar el icono en gris
        }
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {

                    Column {
                        Text(
                            text = "Southbound Field",
                            fontSize = 11.sp,
                            color = TextoGris,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Perfil",
                            fontSize = 20.sp,
                            color = TextoOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {

                    IconButton(onClick = { onNavigateToNotificaciones()}) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notificaciones",
                            tint = TextoOscuro
                        )
                    }


                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape) // Corta el contenedor en forma de círculo perfecto
                            .background(AzulPrincipal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = inicialUsuario,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoPantalla)
            )
        },




        bottomBar = {
            BarraNavegacion(
                seleccionado = Destino.PERFIL,
                onNavegar = { destino ->
                    when (destino) {
                        Destino.SERVICIOS -> onNavigateToServicios()
                        Destino.DISPONIBILIDAD -> onNavigateToDisponibilidad()
                        Destino.PERFIL -> Unit
                    }
                }
            )
        },
        containerColor = FondoPantalla
    ) { paddingValores ->




        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {




            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                color = VerdeFondoBadge,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(VerdeConfirmado, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "EN TURNO",
                                        fontSize = 11.sp,
                                        color = VerdeConfirmado,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }


                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.size(90.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCBD5E1)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (fotoUrl != null){
                                    AsyncImage(
                                        model = fotoUrl,
                                        contentDescription = "Foto Conductor",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Foto Conductor",
                                        tint = Color.White,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                            }


                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(AzulPrincipal)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verificado",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))


                        Text(
                            text = nombreUsuario,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )

                        Spacer(modifier = Modifier.height(6.dp))


                        Surface(
                            color = AzulSecundario,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = AzulPrincipal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Driver Autorizado Southbound",
                                    fontSize = 12.sp,
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))


                        Surface(
                            color = AzulSecundario.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "4.9",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoOscuro
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFEAB308),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "CALIFICACIÓN",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(TextoGris.copy(alpha = 0.3f))
                                )


                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "142",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoOscuro
                                    )
                                    Text(
                                        text = "SERVICIOS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris
                                    )
                                }


                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(TextoGris.copy(alpha = 0.3f))
                                )


                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "99.2%",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeConfirmado
                                    )
                                    Text(
                                        text = "PUNTUALIDAD",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris
                                    )
                                }
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
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {


                        OpcionPerfilItem(
                            icono = Icons.Default.Badge,
                            titulo = "Editar datos de perfil",
                            subtitulo = "Contacto, teléfono y turno asignado",
                            onClick = { }
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)


                        OpcionPerfilItem(
                            icono = Icons.Default.History,
                            titulo = "Historial de servicios",
                            subtitulo = "Expediciones y traslados finalizados",
                            badgeTexto = "Ver 142",
                            onClick = { }
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)


                        OpcionPerfilItem(
                            icono = Icons.Default.DirectionsCar,
                            titulo = "Documentación y licencias",
                            subtitulo = "Mercedes Sprinter • Patente KJ • 8821",
                            badgeTexto = "Al día",
                            badgeColor = VerdeConfirmado,
                            onClick = { }
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)


                        OpcionPerfilItem(
                            icono = Icons.Default.NotificationsNone,
                            titulo = "Notificaciones y alertas",
                            subtitulo = "Voz operativa, clima y cambios de ruta",
                            onClick = onNavigateToNotificaciones
                        )
                    }
                }
            }




            item {
                Button(
                    onClick = { onCerrarSesion() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoRechazar,
                        contentColor = RojoTextoRechazar
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }




            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Proyecto Lince v1.0.4 • Build 8421",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoGris
                    )
                    Text(
                        text = "Proyecto académico DSY1105",
                        fontSize = 11.sp,
                        color = TextoGris
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
fun OpcionPerfilItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    badgeTexto: String? = null,
    badgeColor: Color = AzulPrincipal,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AzulSecundario),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = AzulPrincipal,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))


            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = titulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )


                    if (badgeTexto == "Al día") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(VerdeConfirmado)
                        )
                    }
                }
                Text(
                    text = subtitulo,
                    fontSize = 11.sp,
                    color = TextoGris
                )
            }
        }


        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeTexto != null) {
                Surface(
                    color = if (badgeColor == VerdeConfirmado) VerdeFondoBadge else AzulSecundario,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = badgeTexto,
                        fontSize = 11.sp,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }


            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir a",
                tint = TextoGris,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}