package com.example.proyectolince.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.ui.theme.*
import kotlinx.coroutines.launch

private val turnosDisponibles = listOf(
    "Mañana (06:00 - 14:00)",
    "Tarde (14:00 - 22:00)",
    "Jornada Completa (08:00 - 18:00)",
    "Nocturno / Aeropuerto (22:00 - 06:00)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    nombreActual: String,
    rolUsuario: String,
    emailUsuario: String,
    oficinaCodigo: String,
    recursoId: String?,
    telefonoActual: String,
    turnoActual: String,
    onGuardarCambios: (nuevoNombre: String, nuevoTelefono: String, nuevoTurno: String) -> Unit,
    onVolver: () -> Unit
) {
    var nombre by remember(nombreActual) { mutableStateOf(nombreActual) }
    var telefono by remember(telefonoActual) { mutableStateOf(telefonoActual) }
    var turnoSeleccionado by remember(turnoActual) {
        mutableStateOf(turnoActual.ifBlank { turnosDisponibles.first() })
    }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val inicialPreview = nombre.trim().firstOrNull()?.uppercase() ?: "D"

    Scaffold(
        containerColor = FondoPantalla,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            text = "Editar Perfil",
                            fontSize = 20.sp,
                            color = TextoOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextoOscuro
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoPantalla)
            )
        },
        bottomBar = {
            Surface(color = FondoPantalla, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Button(
                        onClick = {
                            val nombreLimpio = nombre.trim()
                            val telefonoLimpio = telefono.trim()

                            errorNombre = when {
                                nombreLimpio.isBlank() -> "El nombre no puede estar vacío"
                                nombreLimpio.length < 3 -> "Ingresa nombre y apellido"
                                else -> null
                            }

                            errorTelefono = when {
                                telefonoLimpio.isBlank() -> "Ingresa un teléfono de contacto"
                                telefonoLimpio.length < 8 -> "Teléfono demasiado corto"
                                else -> null
                            }

                            if (errorNombre == null && errorTelefono == null) {
                                scope.launch {
                                    guardando = true
                                    onGuardarCambios(nombreLimpio, telefonoLimpio, turnoSeleccionado)
                                    guardando = false
                                    snackbarHostState.showSnackbar("Datos actualizados correctamente")
                                    onVolver()
                                }
                            }
                        },
                        enabled = !guardando,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulPrincipal,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (guardando) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GUARDAR CAMBIOS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
            // 1. Tarjeta superior: Vista previa de avatar + Credencial Operativa (solo lectura)
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(AzulSecundario),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = inicialPreview,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPrincipal
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nombre.ifBlank { "Sin nombre" },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = AzulSecundario,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = rolUsuario.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulPrincipal,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = FondoPantalla, thickness = 1.dp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = TextoGris,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CREDENCIAL OPERATIVA (SOLO LECTURA)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoGris
                            )
                        }

                        DatoSoloLecturaItem(
                            icono = Icons.Default.Email,
                            etiqueta = "Correo corporativo",
                            valor = emailUsuario.ifBlank { "No registrado" }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                DatoSoloLecturaItem(
                                    icono = Icons.Default.Business,
                                    etiqueta = "Base / Oficina",
                                    valor = oficinaCodigo.uppercase()
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DatoSoloLecturaItem(
                                    icono = Icons.Default.Tag,
                                    etiqueta = "ID Recurso Lince",
                                    valor = recursoId?.let { "#$it" } ?: "Sin asignar"
                                )
                            }
                        }
                    }
                }
            }

            // 2. Tarjeta de datos editables de contacto
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DATOS DE CONTACTO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoGris
                            )
                        }

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = {
                                nombre = it
                                errorNombre = null
                            },
                            label = { Text("Nombre completo") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = AzulPrincipal)
                            },
                            isError = errorNombre != null,
                            supportingText = { errorNombre?.let { Text(it) } },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AzulPrincipal,
                                focusedLabelColor = AzulPrincipal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = telefono,
                            onValueChange = {
                                telefono = it
                                errorTelefono = null
                            },
                            label = { Text("Teléfono operativo / WhatsApp") },
                            placeholder = { Text("+56 9 1234 5678") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = AzulPrincipal)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = errorTelefono != null,
                            supportingText = { errorTelefono?.let { Text(it) } },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AzulPrincipal,
                                focusedLabelColor = AzulPrincipal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 3. Tarjeta de selección de turno operativo
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TURNO OPERATIVO PREFERENTE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoGris
                            )
                        }

                        turnosDisponibles.forEach { turno ->
                            val seleccionado = turno == turnoSeleccionado
                            val bordeColor = if (seleccionado) AzulPrincipal else Color(0xFFE2E8F0)
                            val fondoColor = if (seleccionado) AzulSecundario.copy(alpha = 0.45f) else Color.White

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(fondoColor)
                                    .border(
                                        width = if (seleccionado) 1.5.dp else 1.dp,
                                        color = bordeColor,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { turnoSeleccionado = turno }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = turno,
                                    fontSize = 13.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                                    color = if (seleccionado) AzulPrincipal else TextoOscuro
                                )
                                RadioButton(
                                    selected = seleccionado,
                                    onClick = { turnoSeleccionado = turno },
                                    colors = RadioButtonDefaults.colors(selectedColor = AzulPrincipal)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun DatoSoloLecturaItem(
    icono: ImageVector,
    etiqueta: String,
    valor: String
) {
    Surface(
        color = FondoPantalla,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = TextoGris,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = etiqueta,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoGris
                )
                Text(
                    text = valor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoOscuro
                )
            }
        }
    }
}