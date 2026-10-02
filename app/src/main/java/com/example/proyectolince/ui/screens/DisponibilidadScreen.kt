package com.example.proyectolince.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisponibilidadScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                    ) {
                        Text("PROYECTO LINCE", fontSize = 10.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Disponibilidad", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = TextoOscuro)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
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
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Disponibilidad", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulSecundario
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil", fontSize = 11.sp) }
                )
            }
        },
        containerColor = FondoPantalla
    ) { paddingValores ->

        var isDisponible by remember { mutableStateOf(true) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. Título y descripción
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CONTROL DE TURNOS", color = AzulPrincipal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mi Disponibilidad", color = TextoOscuro, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Gestiona tus jornadas activas y descansos\nprogramados en ruta.", color = TextoGris, fontSize = 14.sp)
                }
            }

            // 2. Tarjeta con Switch
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isDisponible) VerdeConfirmado else RojoRechazar)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isDisponible) "Disponible para nuevas asignaciones" else "No disponible",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextoOscuro,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isDisponible) "El equipo de despacho de Southbound podrá asignarte nuevos servicios." else "No recibirás nuevas asignaciones.",
                                fontSize = 12.sp,
                                color = TextoGris
                            )
                        }
                        Switch(
                            checked = isDisponible,
                            onCheckedChange = { isDisponible = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AzulPrincipal)
                        )
                    }
                }
            }

            // 3. Calendario Dinámico
            item {
                CalendarCard()
            }

            // 4. Botón de Bloqueo
            item {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = AzulPrincipal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bloquear día / Marcar no disponible", fontWeight = FontWeight.Bold)
                }
            }

            // 5. Encabezado de Bloqueos Programados
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Próximos Bloqueos Programados", fontWeight = FontWeight.Bold, color = TextoOscuro, fontSize = 16.sp)
                    Text("2 registros", color = TextoGris, fontSize = 12.sp)
                }
            }

            // 6. Tarjeta fija de Bloqueo (Exactamente como en el mockup)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(FondoPantalla),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Hotel, contentDescription = "Descanso", tint = TextoGris)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Domingo 19", fontWeight = FontWeight.Bold, color = TextoOscuro)
                            Surface(color = AzulSecundario, shape = RoundedCornerShape(6.dp)) {
                                Text("Día completo", color = AzulPrincipal, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarCard() {
    var currentMonth by remember { mutableStateOf(YearMonth.of(2026, 10)) }

    val mesNombre = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        .replaceFirstChar { it.uppercase() }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Controles de mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$mesNombre ${currentMonth.year}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoOscuro)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = AzulSecundario, shape = RoundedCornerShape(16.dp)) {
                        Text("22 Jornadas", color = AzulPrincipal, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Row {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Anterior", tint = TextoGris)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Siguiente", tint = TextoOscuro)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Días de la semana
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEach { day ->
                    Text(day, color = TextoGris, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Construcción matemática de la grilla
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfMonth = currentMonth.atDay(1)
            val daysBefore = firstDayOfMonth.dayOfWeek.value - 1
            val daysInPrevMonth = currentMonth.minusMonths(1).lengthOfMonth()

            var currentDay = 1
            var nextMonthDay = 1

            Column {
                for (row in 0..5) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        for (col in 0..6) {
                            when {
                                row == 0 && col < daysBefore -> {
                                    val dayNum = daysInPrevMonth - daysBefore + col + 1
                                    CalendarDayItem(dayNum.toString(), isCurrentMonth = false, tipoIndicador = 0, weight = 1f)
                                }
                                currentDay <= daysInMonth -> {
                                    // 1: Asignado (azul), 2: Disponible (gris), 3: Bloqueado (X)
                                    val indicador = when (currentDay) {
                                        2, 4, 7, 11, 13, 14, 15, 17, 21, 23, 26, 28, 31 -> 1
                                        19 -> 3
                                        else -> 2
                                    }
                                    val isSelected = (currentDay == 14 && currentMonth.monthValue == 10 && currentMonth.year == 2026)

                                    CalendarDayItem(currentDay.toString(), isCurrentMonth = true, tipoIndicador = indicador, isSelected = isSelected, weight = 1f)
                                    currentDay++
                                }
                                else -> {
                                    CalendarDayItem(nextMonthDay.toString(), isCurrentMonth = false, tipoIndicador = 0, weight = 1f)
                                    nextMonthDay++
                                }
                            }
                        }
                    }
                    if (currentDay > daysInMonth && row >= 4) break
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = FondoPantalla)
            Spacer(modifier = Modifier.height(16.dp))

            // Leyenda
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AzulPrincipal))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Asignado", fontSize = 12.sp, color = TextoGris, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFCBD5E1)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Disponible", fontSize = 12.sp, color = TextoGris, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = TextoGris, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bloqueado", fontSize = 12.sp, color = TextoGris, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun RowScope.CalendarDayItem(day: String, isCurrentMonth: Boolean, tipoIndicador: Int, isSelected: Boolean = false, weight: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(weight).padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isSelected) AzulPrincipal else Color.Transparent)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                color = when {
                    isSelected -> Color.White
                    isCurrentMonth -> TextoOscuro
                    else -> Color.LightGray
                },
                fontWeight = if (isCurrentMonth || isSelected) FontWeight.Medium else FontWeight.Normal,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        when (tipoIndicador) {
            1 -> Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(AzulPrincipal))
            2 -> Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFCBD5E1)))
            3 -> Icon(Icons.Default.Close, contentDescription = null, tint = TextoGris, modifier = Modifier.size(8.dp))
            else -> Box(modifier = Modifier.size(4.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewDisponibilidad() {
    MaterialTheme {
        DisponibilidadScreen()
    }
}