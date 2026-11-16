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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.R
import com.example.proyectolince.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    nombreUsuario: String = "Driver Demo",
    rolUsuario: String = "DRIVER",
    telefonoUsuario: String = "+56 9 8765 4321",
    turnoUsuario: String = "Mañana (06:00 - 14:00)",
    idiomaActual: String = "es",
    onCambiarIdioma: (String) -> Unit = {},
    onVolver: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onNavigateToEditarPerfil: () -> Unit = {},
    onNavigateToServicios: () -> Unit = {},
    onNavigateToDetalle: () -> Unit = {},
    onNavigateToDisponibilidad: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {}
) {
    val inicialUsuario = nombreUsuario.firstOrNull()?.uppercase() ?: "D"
    val etiquetaRol = when (rolUsuario.uppercase().trim()) {
        "DRIVER", "TRANSPORTISTA" -> if (idiomaActual == "en") "Authorized Southbound Driver" else "Driver Autorizado Southbound"
        "GUIA" -> if (idiomaActual == "en") "Authorized Southbound Guide" else "Guía Autorizado Southbound"
        "COORDINADOR" -> if (idiomaActual == "en") "Southbound Operations Coordinator" else "Coordinador Operativo Southbound"
        else -> "$rolUsuario Southbound"
    }

    Scaffold(
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
                            text = stringResource(R.string.titulo_perfil),
                            fontSize = 20.sp,
                            color = TextoOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToNotificaciones() }) {
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
                            .clip(CircleShape)
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
                            .padding(20.dp),
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
                                        text = stringResource(R.string.en_turno),
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
                                    .background(AzulSecundario),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = inicialUsuario,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPrincipal
                                )
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
                                    text = etiquetaRol,
                                    fontSize = 12.sp,
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Bold
                                )
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
                            titulo = stringResource(R.string.editar_perfil_titulo),
                            subtitulo = "$telefonoUsuario • $turnoUsuario",
                            onClick = onNavigateToEditarPerfil
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)

                        SelectorIdiomaItem(
                            idiomaActual = idiomaActual,
                            onSeleccionarIdioma = onCambiarIdioma
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)

                        OpcionPerfilItem(
                            icono = Icons.Default.History,
                            titulo = stringResource(R.string.historial_titulo),
                            subtitulo = stringResource(R.string.historial_subtitulo),
                            onClick = { }
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)

                        OpcionPerfilItem(
                            icono = Icons.Default.DirectionsCar,
                            titulo = stringResource(R.string.docs_titulo),
                            subtitulo = stringResource(R.string.docs_subtitulo),
                            badgeColor = VerdeConfirmado,
                            onClick = { }
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
                        text = stringResource(R.string.cerrar_sesion),
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
                        text = stringResource(R.string.app_name),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoGris
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SelectorIdiomaItem(
    idiomaActual: String,
    onSeleccionarIdioma: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
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
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = AzulPrincipal,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.idioma_titulo),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = stringResource(R.string.idioma_subtitulo),
                    fontSize = 11.sp,
                    color = TextoGris
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("es" to "ES", "en" to "EN").forEach { (codigo, etiqueta) ->
                val activo = idiomaActual.equals(codigo, ignoreCase = true)
                Surface(
                    color = if (activo) AzulPrincipal else FondoPantalla,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSeleccionarIdioma(codigo) }
                ) {
                    Text(
                        text = etiqueta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activo) Color.White else TextoGris,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OpcionPerfilItem(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    badgeColor: Color? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
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
                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = subtitulo,
                    fontSize = 11.sp,
                    color = TextoGris
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badgeColor != null) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(badgeColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextoGris,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}