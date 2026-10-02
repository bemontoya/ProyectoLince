package com.example.proyectolince.ui.screens

import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(){
    Scaffold(
        // barra superior
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
                    ) {
                        Text("PROYECTO LINCE", fontSize = 10.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Detalle", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { }){
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notificaciones", tint = TextoOscuro)
                    }

                    // avatar simulado
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AzulPrincipal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = FondoPantalla)
            )
        },
        // barra de navegacion inferior
        bottomBar = {
            NavigationBar(containerColor = Color.White){
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.AltRoute, contentDescription = null)},
                    label = { Text("Servicios", fontSize = 11.sp)}
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null)},
                    label = { Text("Detalle", fontSize = 11.sp)},
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulSecundario
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null)},
                    label = { Text("Disponibilidad", fontSize = 11.sp)}
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.NotificationsNone, contentDescription = null)},
                    label = { Text("Avisos", fontSize = 11.sp)}
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.PersonOutline, contentDescription = null)},
                    label = { Text("Perfil", fontSize = 11.sp)}
                )
            }
        },
        containerColor = FondoPantalla
    ){ paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            // header, boton de volver, numero de servicio y badge confirmado
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    TextButton(
                        onClick = { },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextoGris,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Volver",
                            fontSize = 15.sp,
                            color = TextoGris,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text(
                            "#LNC-2026",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoGris

                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = VerdeFondoBadge,
                            shape = RoundedCornerShape(16.dp)
                        ){
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(VerdeConfirmado, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "CONFIRMADDO",
                                    fontSize = 11.sp,
                                    color = VerdeConfirmado,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // tarjeta principal de horario, origen/destino y mapa
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column(modifier = Modifier.padding(16.dp)){

                        //hora y badge tipo de servicio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Row(verticalAlignment = Alignment.Bottom){
                                Text(
                                    "08:30",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "AM",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGris,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }

                            Surface(
                                color = AzulSecundario,
                                shape = RoundedCornerShape(12.dp)
                            ){
                                Text(
                                    //transfer (transporte privado de personas)
                                    "TRANSFER PRIVADO",
                                    fontSize = 11.sp,
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        //ruta: el origen y destino con la linea de conexion
                        Row(modifier = Modifier.fillMaxWidth()){
                            // indicador grafico (puntos y linea vertical)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 4.dp, end = 12.dp)
                            ){
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(AzulPrincipal, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(44.dp)
                                        .background(VerdeConfirmado)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(VerdeConfirmado, CircleShape)
                                )
                            }

                            //direccion origen/destino
                            Column {
                                Text(
                                    "ORIGEN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGris
                                )
                                Text(
                                    "Hotel Cumbres Puerto Varas",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Text(
                                    "Lobby Principal",
                                    fontSize = 12.sp,
                                    color = TextoGris
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    "DESTINO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGris
                                )
                                Text(
                                    "Aeropuerto El tepual (Puerto Montt)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Text(
                                    "Terminal Salidas Nacionales",
                                    fontSize = 12.sp,
                                    color = TextoGris
                                )
                            }
                        }

                        Spacer (modifier = Modifier.height(16.dp))

                        // mapa simulado
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                //clip para recortar los bordes del componente y hacerlos redondeados
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEF2F6)),
                            contentAlignment = Alignment.Center
                        ){
                            //surface controla el fondo sobre lo que se va a construir todo lo demás
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 2.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ){
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = AzulPrincipal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column{
                                        Row(verticalAlignment = Alignment.CenterVertically){
                                            Text(
                                                "35 min ",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextoOscuro
                                            )
                                            Text(
                                                "(28 km) • ",
                                                fontSize = 12.sp,
                                                color = TextoGris
                                            )
                                            Text(
                                                "Tráfico normal",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = VerdeConfirmado
                                            )
                                        }
                                        Text(
                                            "Vía Ruta 225 y V-505",
                                            fontSize = 11.sp,
                                            color = TextoGris
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //bloque de las 2 columnas, pasajeros y vehiculo
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    //card de pasajeros
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ){
                        Column(modifier = Modifier.padding(14.dp)){
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    Icons.Default.Group,
                                    contentDescription = null,
                                    tint = AzulPrincipal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "PASAJEROS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGris
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Grupo Silva",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )

                            Spacer (modifier = Modifier.height(4.dp))

                            Surface(
                                color = FondoPantalla,
                                shape = RoundedCornerShape(6.dp)
                            ){
                                Text(
                                    "4 Pax",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoGris,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "CONTACTO TITULAR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoGris
                            )
                            Text(
                                "Juan Silva",
                                fontSize = 12.sp,
                                color = TextoOscuro

                            )
                        }
                    }
                }
            }
        }
    }
}