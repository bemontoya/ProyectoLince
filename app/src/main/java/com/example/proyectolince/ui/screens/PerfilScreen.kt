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

// importación de la paleta de colores definida en ui.theme
import com.example.proyectolince.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen() {
    // Scaffold provee la estructura base de la pantalla Android (Barra superior, contenido y barra inferior)
    Scaffold(

        // 1. BARRA SUPERIOR (TopAppBar)

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // se alinean los títulos a la izquierda usando una columna dentro de la barra
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "PROYECTO LINCE",
                            fontSize = 10.sp,
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
                    // botón de icono para las notificaciones
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notificaciones",
                            tint = TextoOscuro
                        )
                    }

                    // avatar simulado en el header (Círculo azul con la inicial 'M')
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape) // Corta el contenedor en forma de círculo perfecto
                            .background(AzulPrincipal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoPantalla)
            )
        },


        // 2. BARRA DE NAVEGACIÓN INFERIOR (BottomBar)

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
                // pestaña 'Perfil' activa (destacada en azul)
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulSecundario
                    )
                )
            }
        },
        containerColor = FondoPantalla // color gris claro de fondo
    ) { paddingValores ->


        // 3. CONTENIDO PRINCIPAL SCROLLABLE (LazyColumn)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores) // respeta márgenes del Scaffold (TopBar y BottomBar)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // espaciado vertical entre cada ítem
        ) {


            // TARJETA PRINCIPAL DEL CONDUCTOR (Foto, Estado, Nombre y Métricas)

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp), // esquinas redondeadas
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally // centra todo el contenido horizontalmente
                    ) {

                        // Badge "EN TURNO" (Ubicado en la esquina superior derecha)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                color = VerdeFondoBadge,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.align(Alignment.TopEnd) // alinea el indicador a la derecha
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // puntos indicadores verdes de estado activo
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

                        // Foto de Perfil con Badge de Verificado
                        Box(
                            contentAlignment = Alignment.BottomEnd, // el check verificado ubicado abajo a la derecha de la foto
                            modifier = Modifier.size(90.dp)
                        ) {
                            // círculo base de la foto de perfil (simulado)
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCBD5E1)), // fondo gris temporal si no hay imagen
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Foto Conductor",
                                    tint = Color.White,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            // Badge azul de cuenta verificada sobre la foto
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(AzulPrincipal)
                                    .border(2.dp, Color.White, CircleShape), // Borde blanco alrededor de la estrella
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

                        // Nombre del Conductor
                        Text(
                            text = "Matias Jeldres",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Cargo u Ocupación Registrada
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

                        // --- Cautro Bloques de Métricas (Calificación, Servicios, Puntualidad) ---
                        Surface(
                            color = AzulSecundario.copy(alpha = 0.5f), // Fondo azul pastel traslúcido
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly, // Distribuye los 3 bloques de forma equitativa
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Métrica 1: Calificación
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
                                            tint = Color(0xFFEAB308), // Color amarillo para la estrella
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = "CALIFICACIÓN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris
                                    )
                                }

                                // línea divisora vertical entre métricas
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(TextoGris.copy(alpha = 0.3f))
                                )

                                // Métrica 2: Cantidad de Servicios
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "142",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoOscuro
                                    )
                                    Text(
                                        text = "SERVICIOS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris
                                    )
                                }

                                // Línea divisora vertical entre métricas
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(24.dp)
                                        .background(TextoGris.copy(alpha = 0.3f))
                                )

                                // Métrica 3: Puntualidad
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "99.2%",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeConfirmado
                                    )
                                    Text(
                                        text = "PUNTUALIDAD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoGris
                                    )
                                }
                            }
                        }
                    }
                }
            }


            // LISTA DE OPCIONES DEL MENÚ DE PERFIL

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {

                        // Opción 1: Editar datos de perfil
                        OpcionPerfilItem(
                            icono = Icons.Default.Badge,
                            titulo = "Editar datos de perfil",
                            subtitulo = "Contacto, teléfono y turno asignado",
                            onClick = { }
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp) // Separador gris tenue

                        // Opción 2: Historial completo de servicios
                        OpcionPerfilItem(
                            icono = Icons.Default.History,
                            titulo = "Historial completo de serv...",
                            subtitulo = "Expediciones y traslados fin...",
                            badgeTexto = "Ver 142", // Muestra el total de servicios terminados
                            onClick = { }
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)

                        // Opción 3: Documentación y licencias
                        OpcionPerfilItem(
                            icono = Icons.Default.DirectionsCar,
                            titulo = "Documentación y licencias",
                            subtitulo = "Mercedes-Benz Sprinter • Patent...",
                            badgeTexto = "Al día",
                            badgeColor = VerdeConfirmado, // Texto verde "Al día"
                            onClick = { }
                        )

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)

                        // Opción 4: Notificaciones y alertas
                        OpcionPerfilItem(
                            icono = Icons.Default.NotificationsNone,
                            titulo = "Notificaciones y alertas",
                            subtitulo = "Voz operativa, clima y cambios de ruta",
                            onClick = { }
                        )
                    }
                }
            }


            // BOTÓN DE CERRAR SESIÓN

            item {
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoRechazar, // fondo rosado suave (#FEE2E2)
                        contentColor = RojoTextoRechazar // texto e icono rojo (#DC2626)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Cerrar Sesión",
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


            // PIE DE PÁGINA (Información de la versión de la app)

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
                        text = "Southbound Operaciones Patagonia",
                        fontSize = 11.sp,
                        color = TextoGris
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// =================================================================================================
// COMPONENTES REUTILIZABLES INTERNOS
// =================================================================================================

/**
 * Función composable auxiliar para renderizar cada fila de opción en el menú de perfil.
 * Evita repetir código en las 4 opciones principales.
 */
@Composable
fun OpcionPerfilItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    badgeTexto: String? = null, // Parámetro opcional por si la opción lleva una etiqueta (ej: "Al día")
    badgeColor: Color = AzulPrincipal,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Contenedor cuadrado con bordes redondeados para el icono de la izquierda
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

            // Textos de Título y Subtítulo
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = titulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    // Si la opción requiere un punto verde (Ej: Documentación)
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

        // Elementos en el extremo derecho (Badge opcional + Flecha de navegación)
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

            // Flecha indicadora 'chevron_right'
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir a",
                tint = TextoGris,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}