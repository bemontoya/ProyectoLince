package com.example.proyectolince.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.R
import com.example.proyectolince.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private enum class TipoDia { SERVICIO_PENDIENTE, SERVICIO_CONFIRMADO, BLOQUEADO }

private data class Bloqueo(val fecha: LocalDate, val motivo: String)

private data class EstiloDia(
    val relleno: Color,
    val texto: Color,
    val indicador: Color,
    @StringRes val etiquetaRes: Int
)

private fun estiloDe(tipo: TipoDia): EstiloDia = when (tipo) {
    TipoDia.SERVICIO_PENDIENTE -> EstiloDia(
        NaranjaFondoBadge, NaranjaPendiente, NaranjaPendiente,
        R.string.estado_pendiente
    )
    TipoDia.SERVICIO_CONFIRMADO -> EstiloDia(
        VerdeFondoBadge, VerdeConfirmado, VerdeConfirmado,
        R.string.estado_confirmado
    )
    TipoDia.BLOQUEADO -> EstiloDia(
        RojoRechazar, RojoTextoRechazar, RojoTextoRechazar,
        R.string.estado_bloqueado
    )
}

private fun diasConServicioDemo(): Map<LocalDate, TipoDia> {
    val hoy = LocalDate.now()
    return mapOf(
        hoy.plusDays(1) to TipoDia.SERVICIO_PENDIENTE,
        hoy.plusDays(3) to TipoDia.SERVICIO_CONFIRMADO,
        hoy.plusDays(4) to TipoDia.SERVICIO_CONFIRMADO
    )
}

private fun fechaLarga(fecha: LocalDate, locale: Locale): String {
    val dia = fecha.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar {
        it.uppercase()
    }
    val mes = fecha.month.getDisplayName(TextStyle.FULL, locale)
    return if (locale.language == "en") {
        "$dia, $mes ${fecha.dayOfMonth}"
    } else {
        "$dia ${fecha.dayOfMonth} de $mes"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisponibilidadScreen(
    inicialUsuario: String = "D",
    onNavigateToServicios: () -> Unit = {},
    onNavigateToDetalle: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    val hoy = remember { LocalDate.now() }
    val fechaMinimaBloqueo = remember(hoy) { hoy.plusDays(7) }
    val localeActual = LocalConfiguration.current.locales[0] ?: Locale.getDefault()

    var isDisponible by rememberSaveable { mutableStateOf(true) }
    var mes by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var fechaSeleccionada by rememberSaveable { mutableStateOf<LocalDate?>(hoy) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val serviciosDemo = remember { diasConServicioDemo() }
    val bloqueos = remember {
        mutableStateListOf(Bloqueo(hoy.plusDays(10), "Descanso programado / Scheduled rest"))
    }

    val tiposPorFecha: Map<LocalDate, TipoDia> = buildMap {
        putAll(serviciosDemo)
        bloqueos.forEach { put(it.fecha, TipoDia.BLOQUEADO) }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val msgDesbloqueado = stringResource(R.string.msg_dia_desbloqueado)
    val msgBloqueado = stringResource(R.string.msg_dia_bloqueado)
    val msgServicioAsignado = stringResource(R.string.error_servicio_asignado)
    val fechaMinimaStr = fechaMinimaBloqueo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val msg7Dias = stringResource(R.string.error_7_dias, fechaMinimaStr)

    fun alternarBloqueo() {
        val fecha = fechaSeleccionada ?: return
        val existente = bloqueos.find { it.fecha == fecha }
        when {
            existente != null -> {
                bloqueos.remove(existente)
                scope.launch { snackbarHostState.showSnackbar(msgDesbloqueado) }
            }
            serviciosDemo.containsKey(fecha) -> {
                scope.launch { snackbarHostState.showSnackbar(msgServicioAsignado) }
            }
            fecha.isBefore(fechaMinimaBloqueo) -> {
                scope.launch { snackbarHostState.showSnackbar(msg7Dias) }
            }
            else -> mostrarDialogo = true
        }
    }

    val yaBloqueado = fechaSeleccionada != null && bloqueos.any { it.fecha == fechaSeleccionada }
    val bloqueosOrdenados = bloqueos.sortedBy { it.fecha }

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
                            text = stringResource(R.string.titulo_disponibilidad),
                            fontSize = 20.sp,
                            color = TextoOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = TextoOscuro)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
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
            BarraNavegacion(
                seleccionado = Destino.DISPONIBILIDAD,
                onNavegar = { destino ->
                    when (destino) {
                        Destino.SERVICIOS -> onNavigateToServicios()
                        Destino.DISPONIBILIDAD -> Unit
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.control_turnos),
                            color = AzulPrincipal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.mi_disponibilidad),
                        color = TextoOscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.subtitulo_disponibilidad),
                        color = TextoGris,
                        fontSize = 14.sp
                    )
                }
            }

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
                                        .background(if (isDisponible) VerdeConfirmado else RojoTextoRechazar)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isDisponible) stringResource(R.string.disponible_nuevas)
                                    else stringResource(R.string.no_disponible),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextoOscuro
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isDisponible) stringResource(R.string.desc_disponible)
                                else stringResource(R.string.desc_no_disponible),
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

            // Aviso regla operativa de 7 días
            item {
                Surface(
                    color = AzulSecundario,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.regla_7_dias),
                            fontSize = 12.sp,
                            color = AzulPrincipal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                CalendarCard(
                    mes = mes,
                    onMesChange = { mes = it },
                    hoy = hoy,
                    fechaSeleccionada = fechaSeleccionada,
                    onSeleccionar = { fechaSeleccionada = it },
                    tiposPorFecha = tiposPorFecha,
                    locale = localeActual
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val seleccionada = fechaSeleccionada
                    if (seleccionada != null) {
                        val tipo = tiposPorFecha[seleccionada]
                        val estado = if (tipo != null) stringResource(estiloDe(tipo).etiquetaRes)
                        else stringResource(R.string.estado_disponible)
                        Text(
                            text = "${fechaLarga(seleccionada, localeActual)}\n$estado",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                    }

                    OutlinedButton(
                        onClick = { alternarBloqueo() },
                        enabled = seleccionada != null,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro)
                    ) {
                        Icon(
                            if (yaBloqueado) Icons.Default.LockOpen else Icons.Default.Block,
                            contentDescription = null,
                            tint = AzulPrincipal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (yaBloqueado) stringResource(R.string.desbloquear_dia_btn)
                            else stringResource(R.string.bloquear_dia_btn),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.proximos_bloqueos),
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${bloqueos.size}",
                        color = TextoGris,
                        fontSize = 12.sp
                    )
                }
            }

            if (bloqueosOrdenados.isEmpty()) {
                item {
                    Text(stringResource(R.string.sin_dias_bloqueados), fontSize = 13.sp, color = TextoGris)
                }
            }

            items(bloqueosOrdenados, key = { it.fecha.toEpochDay() }) { bloqueo ->
                BloqueoCard(
                    bloqueo = bloqueo,
                    locale = localeActual,
                    onDesbloquear = {
                        bloqueos.remove(bloqueo)
                        scope.launch { snackbarHostState.showSnackbar(msgDesbloqueado) }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        if (mostrarDialogo) {
            fechaSeleccionada?.let { fecha ->
                MotivoBloqueoDialog(
                    fechaTexto = fechaLarga(fecha, localeActual),
                    onConfirmar = { motivo ->
                        bloqueos.add(Bloqueo(fecha, motivo))
                        mostrarDialogo = false
                        scope.launch { snackbarHostState.showSnackbar(msgBloqueado) }
                    },
                    onCancelar = { mostrarDialogo = false }
                )
            }
        }
    }
}

@Composable
private fun BloqueoCard(bloqueo: Bloqueo, locale: Locale, onDesbloquear: () -> Unit) {
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
                Icon(Icons.Default.Hotel, contentDescription = null, tint = TextoGris)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(fechaLarga(bloqueo.fecha, locale), fontWeight = FontWeight.Bold, color = TextoOscuro)
                Text(bloqueo.motivo, fontSize = 12.sp, color = TextoGris)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(color = AzulSecundario, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = stringResource(R.string.dia_completo),
                        color = AzulPrincipal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            IconButton(onClick = onDesbloquear) {
                Icon(Icons.Default.Close, contentDescription = null, tint = TextoGris)
            }
        }
    }
}

@Composable
private fun MotivoBloqueoDialog(
    fechaTexto: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(stringResource(R.string.bloquear_dia_btn)) },
        text = {
            Column {
                Text(fechaTexto, fontSize = 13.sp, color = TextoGris)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it; error = null },
                    label = { Text("Motivo / Reason") },
                    isError = error != null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (motivo.isNotBlank()) onConfirmar(motivo.trim())
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancel") } }
    )
}

@Composable
private fun CalendarCard(
    mes: YearMonth,
    onMesChange: (YearMonth) -> Unit,
    hoy: LocalDate,
    fechaSeleccionada: LocalDate?,
    onSeleccionar: (LocalDate) -> Unit,
    tiposPorFecha: Map<LocalDate, TipoDia>,
    locale: Locale
) {
    val mesNombre = mes.month.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() }

    val servicios = tiposPorFecha.count { (fecha, tipo) ->
        YearMonth.from(fecha) == mes && tipo != TipoDia.BLOQUEADO
    }

    val diasSemana = if (locale.language == "en") {
        listOf("M", "T", "W", "T", "F", "S", "S")
    } else {
        listOf("L", "M", "M", "J", "V", "S", "D")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$mesNombre ${mes.year}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoOscuro)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = AzulSecundario, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            text = "$servicios",
                            color = AzulPrincipal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Row {
                    IconButton(onClick = { onMesChange(mes.minusMonths(1)) }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = TextoGris)
                    }
                    IconButton(onClick = { onMesChange(mes.plusMonths(1)) }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextoOscuro)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                diasSemana.forEach { dia ->
                    Text(
                        dia,
                        color = TextoGris,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val huecos = (mes.atDay(1).dayOfWeek.value - 1) % 7
            val celdas: List<LocalDate?> = List(huecos) { null } + (1..mes.lengthOfMonth()).map { mes.atDay(it) }

            celdas.chunked(7).forEach { fila ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    fila.forEach { fecha ->
                        if (fecha == null) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            DiaCalendario(
                                fecha = fecha,
                                tipo = tiposPorFecha[fecha],
                                esHoy = fecha == hoy,
                                seleccionado = fecha == fechaSeleccionada,
                                onClick = { onSeleccionar(fecha) }
                            )
                        }
                    }
                    repeat(7 - fila.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = FondoPantalla)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ItemLeyenda(Modifier.weight(1f), stringResource(R.string.estado_pendiente)) {
                        PuntoColor(estiloDe(TipoDia.SERVICIO_PENDIENTE).indicador)
                    }
                    ItemLeyenda(Modifier.weight(1f), stringResource(R.string.estado_confirmado)) {
                        PuntoColor(estiloDe(TipoDia.SERVICIO_CONFIRMADO).indicador)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    ItemLeyenda(Modifier.weight(1f), stringResource(R.string.estado_bloqueado)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = RojoTextoRechazar, modifier = Modifier.size(12.dp))
                    }
                    ItemLeyenda(Modifier.weight(1f), stringResource(R.string.leyenda_hoy)) {
                        Box(modifier = Modifier.size(10.dp).border(1.5.dp, AzulPrincipal, CircleShape))
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DiaCalendario(
    fecha: LocalDate,
    tipo: TipoDia?,
    esHoy: Boolean,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val estilo = tipo?.let { estiloDe(it) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    when {
                        seleccionado -> AzulPrincipal
                        estilo != null -> estilo.relleno
                        else -> Color.Transparent
                    }
                )
                .then(
                    if (esHoy && !seleccionado) Modifier.border(1.5.dp, AzulPrincipal, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = fecha.dayOfMonth.toString(),
                color = when {
                    seleccionado -> Color.White
                    estilo != null -> estilo.texto
                    else -> TextoOscuro
                },
                fontWeight = if (estilo != null || esHoy) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        when (tipo) {
            TipoDia.BLOQUEADO ->
                Icon(Icons.Default.Close, contentDescription = null, tint = RojoTextoRechazar, modifier = Modifier.size(8.dp))
            null ->
                Box(modifier = Modifier.size(4.dp))
            else ->
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(estilo?.indicador ?: Color.Transparent))
        }
    }
}

@Composable
private fun ItemLeyenda(modifier: Modifier, texto: String, marca: @Composable () -> Unit) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        marca()
        Spacer(modifier = Modifier.width(6.dp))
        Text(texto, fontSize = 12.sp, color = TextoGris, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PuntoColor(color: Color) {
    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
}

@Preview(showBackground = true)
@Composable
fun PreviewDisponibilidad() {
    MaterialTheme {
        DisponibilidadScreen()
    }
}