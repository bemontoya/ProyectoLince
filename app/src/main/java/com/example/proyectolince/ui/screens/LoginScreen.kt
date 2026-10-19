package com.example.proyectolince.ui.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectolince.R
import com.example.proyectolince.ui.theme.*



// enum es una lista cerrada de opciones.
// cada rol guarda su texto y su ícono, así se evita repetir codigo
enum class Rol(val etiqueta: String, val icono: ImageVector) {
    DRIVER("Driver", Icons.Default.DirectionsCar),
    GUIA("Guía", Icons.Default.Explore),
    COORDINADOR("Coordinador", Icons.Default.Hub)
}

// "data class" = una ficha de datos que representa un usuario ficticio
data class UsuarioFicticio(
    val correo: String,
    val clave: String,
    val rol: Rol
)

// lista temporal de usuarios de prueba
// esta lista se reemplaza por una consulta al Repository.
private val usuariosDemo = listOf(
    UsuarioFicticio("driver01@demo.lince", "Lince2026", Rol.DRIVER),
    UsuarioFicticio("guia01@demo.lince", "Lince2026", Rol.GUIA),
    UsuarioFicticio("coord01@demo.lince", "Lince2026", Rol.COORDINADOR)
)



// devuelve un texto de error si algo está mal, o null si todo está bien.
private fun validarLogin(correo: String, clave: String, rol: Rol): String? {
    if (correo.isBlank()) return "Ingresa tu correo"
    // Patterns.EMAIL_ADDRESS revisa que tenga formato de correo (algo@algo.algo)
    if (!Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) return "El correo no es válido"
    if (clave.isBlank()) return "Ingresa tu contraseña"

    // busca un usuario que coincida con correo y contraseña
    val usuario = usuariosDemo.find {
        it.correo.equals(correo.trim(), ignoreCase = true) && it.clave == clave
    } ?: return "Correo o contraseña incorrectos"

    // validación de rol si eligió la pestaña equivocada, no entra.
    if (usuario.rol != rol) return "Este usuario no tiene el rol ${rol.etiqueta}"

    return null
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    // Ahora avisamos QUÉ rol entró (antes no recibía nada).
    // Así la navegación puede abrir pantallas distintas según el rol.
    onLoginSuccess: (Rol) -> Unit = {}
) {
    // ---- Estados (variables que, al cambiar, redibujan la pantalla) ----
    var rolSeleccionado by remember { mutableStateOf(Rol.DRIVER) }
    // Los campos parten VACÍOS: antes tenías un correo y una clave escritos en el código
    var usuarioText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    // String? significa "texto o nada". null = no hay error que mostrar.
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Scaffold(containerColor = FondoPantalla) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ---- BLOQUE SUPERIOR: logo y títulos (sin cambios) ----
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_lince),
                    contentDescription = "Logo Southbound Field",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Southbound Field",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(VerdeConfirmado, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Operaciones y asignaciones",
                        fontSize = 13.sp,
                        color = TextoGris,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ---- BLOQUE CENTRAL: formulario ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Selector de rol
                Surface(
                    color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Antes eran 3 botones casi iguales (mucho código repetido).
                        // Ahora recorremos la lista de roles y dibujamos una pestaña por cada uno.
                        Rol.values().forEach { rol ->
                            RolTab(
                                rol = rol,
                                seleccionado = rolSeleccionado == rol,
                                onClick = {
                                    rolSeleccionado = rol
                                    mensajeError = null // al cambiar de rol, borramos el error anterior
                                }
                            )
                        }
                    }
                }

                // Campo correo
                Column {
                    Text(
                        text = "CORREO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoGris,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = usuarioText,
                        onValueChange = {
                            usuarioText = it
                            mensajeError = null // si el usuario escribe, el error desaparece
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        // placeholder = texto gris de ayuda cuando el campo está vacío
                        placeholder = { Text("driver01@demo.lince", color = TextoGris) },
                        leadingIcon = {
                            Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = TextoGris)
                        },
                        // isError pinta el borde de rojo cuando hay un mensaje de error
                        isError = mensajeError != null,
                        // Teclado con la arroba a mano
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AzulPrincipal,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                // Campo contraseña
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTRASEÑA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoGris
                        )
                        // Por ahora no hace nada (es un MVP con datos ficticios)
                        TextButton(onClick = { }) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPrincipal
                            )
                        }
                    }
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = {
                            passwordText = it
                            mensajeError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextoGris)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = TextoGris
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = mensajeError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AzulPrincipal,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                // Mensaje de error: solo se dibuja si mensajeError NO es null.
                // "?.let { }" significa: "si hay texto, haz esto con él".
                mensajeError?.let { texto ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = texto,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }


                // Botón iniciar sesión
                Button(
                    onClick = {
                        // 1) Validamos todo con la función de arriba
                        val error = validarLogin(usuarioText, passwordText, rolSeleccionado)
                        if (error == null) {
                            // 2) Sin errores: avisamos qué rol entró
                            onLoginSuccess(rolSeleccionado)
                        } else {
                            // 3) Con error: lo guardamos en el estado y la pantalla lo muestra
                            mensajeError = error
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// COMPONENTE REUTILIZABLE: una pestaña del selector de rol
// ─────────────────────────────────────────────────────────────

// "RowScope." permite usar Modifier.weight(1f) dentro de un Row.
// Así las 3 pestañas reparten el ancho en partes iguales.
@Composable
private fun RowScope.RolTab(
    rol: Rol,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            // Si está seleccionada: fondo blanco. Si no: transparente.
            containerColor = if (seleccionado) Color.White else Color.Transparent,
            contentColor = if (seleccionado) TextoOscuro else TextoGris
        ),
        elevation = if (seleccionado) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
        modifier = Modifier
            .weight(1f)
            .height(40.dp) // la misma altura para las 3: esto arregla el desalineado de tu captura
    ) {
        Icon(rol.icono, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(rol.etiqueta, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}