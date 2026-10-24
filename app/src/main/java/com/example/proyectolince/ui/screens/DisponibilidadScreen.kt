package com.example.proyectolince.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


// un dia normal sin nada no aparece aqui
private enum class TipoDia { SERVICIO_PENDIENTE, SERVICIO_CONFIRMADO, BLOQUEADO }

//un dia que el driver bloqueó con el motivo
private data class Bloqueo (val fecha: LocalDate, val motivo: String)

private data class EstiloDia(
    val relleno: Color,
    val texto: Color,
    val indicador: Color,
    val etiqueta: String
)

private fun estiloDe(tipo: TipoDia): EstiloDia = when (tipo) {
    TipoDia.SERVICIO_PENDIENTE -> EstiloDia(
        NaranjaFondoBadge, NaranjaPendiente, NaranjaPendiente,
        "Servicio pendiente"
    )

    TipoDia.SERVICIO_CONFIRMADO -> EstiloDia(
        VerdeFondoBadge, VerdeConfirmado, VerdeConfirmado,
        "Servicio confirmado"
    )

    TipoDia.BLOQUEADO -> EstiloDia(
        RojoRechazar, RojoTextoRechazar, RojoTextoRechazar,
        "Bloqueado"
    )
}

//datos ficticios

private fun diasConServicioDemo(): Map<LocalDate, TipoDia> {
    val mes = YearMonth.now()
    return mapOf(
        mes.atDay(12) to TipoDia.SERVICIO_PENDIENTE,
        mes.atDay(14) to TipoDia.SERVICIO_CONFIRMADO,
        mes.atDay(15) to TipoDia.SERVICIO_CONFIRMADO
    )
}

//para que nombres de los dias y los meses salgan siempre en español
private val localeCl: Locale = Locale.forLanguageTag("es-CL")

//convierte fecha en texto
private fun fechaLarga(fecha: LocalDate): String {
    val dia = fecha.dayOfWeek.getDisplayName(TextStyle.FULL, localeCl).replaceFirstChar{
        it.uppercase()
    }
    val mes = fecha.month.getDisplayName(TextStyle.FULL, localeCl)
    return "$dia ${fecha.dayOfMonth} de $mes"
}


//pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisponibilidadScreen(
    inicialUsuario: String = "D",
    onNavigateToServicios: () -> Unit = {},
    onNavigateToDetalle: () -> Unit = {},
    onNavigateToNotificaciones: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}

){
    //estados
    val hoy = remember { LocalDate.now() }

    //remember Saveable para que no se pierda al girar el telefono , LocalDate y YearMonth se pueden guardar

    var isDisponible by rememberSaveable { mutableStateOf(true) }
    var mes by rememberSaveable {mutableStateOf(YearMonth.now()) }
    var fechaSeleccionada by rememberSaveable {mutableStateOf<LocalDate?>(hoy) }
    var mostrarDialogo by remember {mutableStateOf(false)}

    //servicio de ejemplo
    val serviciosDemo = remember {diasConServicioDemo()}
    val bloqueos = remember {
        mutableStateListOf(Bloqueo(YearMonth.now().atDay(18), "Descanso programado"))

    }

    //une los servicios ++ bloqueos en un soolo mapa: fecha -> tipo dia
    //el calendario consulta este mapa para saber de que color pintar cada dia
    val tiposPorFecha: Map<LocalDate, TipoDia> = buildMap{
        putAll(serviciosDemo)
        bloqueos.forEach {put(it.fecha, TipoDia.BLOQUEADO)}
    }

    val snackbarHostState = remember {SnackbarHostState()}
    val scope = rememberCoroutineScope()

    //logica para el boton de bloquear / desbloquear
    fun alternarBloqueo() {
        val fecha = fechaSeleccionada ?: return
        val existente = bloqueos.find { it.fecha == fecha}
        when {

            //empieza bloqueado y se desbloquea
            existente != null -> {
                bloqueos.remove(existente)
                scope.launch{snackbarHostState.showSnackbar("Día desbloqueado")}
            }

            //tiene un servicio asignado, no se puede bloquear porque habría un conflicto
            serviciosDemo.containsKey(fecha) -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Ese día tiene un servicio asignado. Recházalo primero para bloquearlo.")

                }
            }

            //dia libre: se pide el motivo antes de bloquear
            else -> mostrarDialogo = true

        }
    }

    val yaBloqueado = fechaSeleccionada != null && bloqueos.any {it.fecha == fechaSeleccionada}
    val bloqueosOrdenados = bloqueos.sortedBy {it.fecha} //del mas cercano al mas lejano

    Scaffold(
        containerColor = FondoPantalla,
        snackbarHost = {SnackbarHost(snackbarHostState)},

        //barra alineada a la izquierda
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Southbound Field", fontSize = 11.sp, color = TextoGris, fontWeight = FontWeight.Bold)
                        Text("Disponibilidad", fontSize = 20.sp, color = TextoOscuro, fontWeight = FontWeight.Bold)

                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotificaciones) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = TextoOscuro)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AzulPrincipal)
                            .clickable { onNavigateToPerfil()},
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
                onNavegar = {destino ->
                    when (destino){
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

            // titulo y descripcion
            item {
                Column(modifier = Modifier.padding(top = 8.dp)){
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Control de turnos", color = AzulPrincipal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mi disponibilidad", color = TextoOscuro, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Gestiona tus jornadas activas y descansos programados en ruta.", color = TextoGris, fontSize = 14.sp)
                }
            }

            // tarjeta con interruptor de disponibilidad general
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween

                    ){
                        Column(modifier = Modifier.weight(1f)){
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if(isDisponible) VerdeConfirmado else RojoTextoRechazar)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isDisponible) "Disponible para nuevas asignaciones" else "No disponible",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextoOscuro
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isDisponible) "El equipo de despacho podrá asignarte nuevos servicios." else "No recibirás nuevas asignaciones.",
                                fontSize = 12.sp,
                                color = TextoGris
                            )
                        }
                        Switch(
                            checked = isDisponible,
                            onCheckedChange = {isDisponible = it},
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AzulPrincipal)
                        )
                    }
                }
            }

            //calendario con los dias importantes marcados en colores
            item {
                CalendarCard(
                    mes = mes,
                    onMesChange = {mes = it},
                    hoy = hoy,
                    fechaSeleccionada = fechaSeleccionada,
                    onSeleccionar = {fechaSeleccionada = it},
                    tiposPorFecha = tiposPorFecha
                )
            }
            //estado del dia seleccionado
            item{
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)){
                    val seleccionada = fechaSeleccionada
                    if (seleccionada != null){
                        //ejemplo
                        val tipo = tiposPorFecha[seleccionada]
                        val estado = if(tipo != null) estiloDe(tipo).etiqueta else "Disponible"
                        Text(
                            "${fechaLarga(seleccionada)} • $estado",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                    } else {
                        Text("Toca un día del calendario para ver su estado.", fontSize = 13.sp, color = TextoGris)
                    }

                    OutlinedButton(
                        onClick = {alternarBloqueo()},
                        enabled = seleccionada != null,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro)

                    ){
                        Icon(
                            if (yaBloqueado) Icons.Default.LockOpen else Icons.Default.Block,
                            contentDescription = null,
                            tint = AzulPrincipal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        //el texto del boton cambia segun el dia seleccionado
                        Text(
                            if (yaBloqueado) "Desbloquear día seleccionado" else "Bloquear día seleccionado",
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }

            //encabezado de la lista de bloqueos
            item{
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("Próximos bloqueos", fontWeight = FontWeight.Bold, color = TextoOscuro, fontSize = 16.sp )
                    Text(
                        if (bloqueos.size == 1) "1 registro" else "${bloqueos.size} registros",
                        color = TextoGris,
                        fontSize = 12.sp
                    )
                }
            }

            //lista de bloqueos, una tarjeta por cada dia bloqueado
            if (bloqueosOrdenados.isEmpty()){
                item{
                    Text("No tienes días bloqueados.", fontSize = 13.sp, color = TextoGris )
                }
            }

            //key = fecha: ayuda a compose a no confundir tarjetas cuando la lista cambia
            items(bloqueosOrdenados, key = {it.fecha.toEpochDay()}) {bloqueo ->
                BloqueoCard(
                    bloqueo = bloqueo,
                    onDesbloquear = {
                        bloqueos.remove(bloqueo)
                        scope.launch { snackbarHostState.showSnackbar("Día desbloqueado") }
                    }
                )
            }

            item {Spacer(modifier = Modifier.height(16.dp))}

        }
        if (mostrarDialogo){
            fechaSeleccionada?.let { fecha ->
                MotivoBloqueoDialog(
                    fechaTexto = fechaLarga(fecha),
                    onConfirmar = {motivo ->
                        bloqueos.add(Bloqueo(fecha, motivo)) // el calendario se repinta solo
                        mostrarDialogo = false
                        scope.launch{snackbarHostState.showSnackbar("Día bloqueado")}
                    },
                    onCancelar = {mostrarDialogo = false}
                )
            }
        }
    }
}

//tarjeta de un dia bloqueado

@Composable
private fun BloqueoCard(bloqueo: Bloqueo, onDesbloquear: () -> Unit){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ){
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
            Column(modifier = Modifier.weight(1f)){
                Text(fechaLarga(bloqueo.fecha), fontWeight = FontWeight.Bold, color = TextoOscuro)
                Text(bloqueo.motivo, fontSize = 12.sp, color = TextoGris) //motivo que escribió el usuario
                Spacer(modifier = Modifier.height(4.dp))
                Surface(color = AzulSecundario, shape = RoundedCornerShape(6.dp)){
                    Text(
                        "Día completo",
                        color = AzulPrincipal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            IconButton(onClick = onDesbloquear){
                Icon(Icons.Default.Close, contentDescription = "Desbloquear día", tint = TextoGris)
            }
        }
    }
}


//componente del dialogo para pedir motivo del bloqueo

@Composable
private fun MotivoBloqueoDialog(
    fechaTexto: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
){
    var motivo by remember {mutableStateOf("")}
    var error by remember {mutableStateOf<String?>(null)}

    AlertDialog(
        onDismissRequest = onCancelar,
        title = {Text("Bloquear día")},
        text = {
            Column{
                Text("$fechaTexto. Indica el motivo para que coordinación lo sepa.", fontSize = 13.sp, color = TextoGris)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = motivo,
                    onValueChange = {motivo = it; error = null},
                    label = {Text("Motivo")},
                    isError = error != null,
                    supportingText = {error?.let {Text(it)}},
                    modifier = Modifier.fillMaxWidth()


                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                //campo obligatorio antes de guardar
                if (motivo.isBlank()) error = "Ingresa el motivo del bloqueo"
                else onConfirmar(motivo.trim())

            }) {Text("Bloquear")}
        },
        dismissButton = {TextButton(onClick = onCancelar) {Text("cancelar")} }
    )
}

//componente para el calendario
@Composable
private fun CalendarCard(
    mes: YearMonth,
    onMesChange: (YearMonth) -> Unit,
    hoy: LocalDate,
    fechaSeleccionada: LocalDate?,
    onSeleccionar: (LocalDate) -> Unit,
    tiposPorFecha: Map<LocalDate, TipoDia>
){
    val mesNombre = mes.month.getDisplayName(TextStyle.FULL, localeCl).replaceFirstChar {it.uppercase()}

    //cuantos dias con servicio hay en este mes
    val servicios = tiposPorFecha.count {(fecha, tipo) ->
        YearMonth.from(fecha) == mes && tipo != TipoDia.BLOQUEADO
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier.padding(16.dp)){
            //controles de mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text("$mesNombre ${mes.year}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoOscuro)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = AzulSecundario, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            if (servicios == 1) "1 servicio" else "$servicios servicios",
                            color = AzulPrincipal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Row {

                    IconButton(onClick = { onMesChange(mes.minusMonths(1)) }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior", tint = TextoGris)
                    }
                    IconButton(onClick = { onMesChange(mes.plusMonths(1)) }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente", tint = TextoOscuro)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // dias de la semana
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEach { dia ->
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

            val huecos = mes.atDay(1).dayOfWeek.value - 1
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
                    ItemLeyenda(Modifier.weight(1f), "Servicio pendiente") {
                        PuntoColor(estiloDe(TipoDia.SERVICIO_PENDIENTE).indicador)
                    }
                    ItemLeyenda(Modifier.weight(1f), "Servicio confirmado") {
                        PuntoColor(estiloDe(TipoDia.SERVICIO_CONFIRMADO).indicador)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    ItemLeyenda(Modifier.weight(1f), "Bloqueado") {
                        Icon(Icons.Default.Close, contentDescription = null, tint = RojoTextoRechazar, modifier = Modifier.size(12.dp))
                    }
                    ItemLeyenda(Modifier.weight(1f), "Hoy") {

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
    val estilo = tipo?.let { estiloDe(it) }   // null si es un día normal

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
                // los días importantes y hoy van en negrita para destacar
                fontWeight = if (estilo != null || esHoy) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // indicador bajo el número. el bloqueado usa una X.
        when (tipo) {
            TipoDia.BLOQUEADO ->
                Icon(Icons.Default.Close, contentDescription = "Bloqueado", tint = RojoTextoRechazar, modifier = Modifier.size(8.dp))
            null ->
                Box(modifier = Modifier.size(4.dp))   // día normal: espacio vacío para que todo quede alineado
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


