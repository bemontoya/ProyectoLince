package com.example.proyectolince.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectolince.R
import com.example.proyectolince.ui.theme.*
import com.example.proyectolince.ui.viewmodel.LoginViewModel

enum class Rol(val etiqueta: String, val icono: ImageVector){
    DRIVER("Driver", Icons.Default.DirectionsCar),
    GUIA("Guía", Icons.Default.Explore),
    COORDINADOR("Coordinador", Icons.Default.Hub)
}

data class UsuarioFicticio(
    val correo: String,
    val clave: String,
    val rol: Rol
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (Rol) -> Unit = {}
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess(uiState.rolSeleccionado)
            loginViewModel.resetSuccessState()
        }
    }

    Scaffold(
        containerColor = FondoPantalla,
        modifier = Modifier.imePadding()
    ) { paddingValores ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // Logo y Encabezado
                Image(
                    painter = painterResource(id = R.drawable.logo_lince),
                    contentDescription = "Logo Southbound Field",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Southbound Field",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(4.dp))
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

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de Rol
                Surface(
                    color = Color(0xFFE2E8F0).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Rol.values().forEach { rol ->
                            RolTab(
                                rol = rol,
                                seleccionado = uiState.rolSeleccionado == rol,
                                onClick = { loginViewModel.onRolSelected(rol) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Correo
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CORREO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoGris,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = { loginViewModel.onCorreoChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("driver01@demo.lince", color = TextoGris) },
                        leadingIcon = {
                            Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = TextoGris)
                        },
                        isError = uiState.mensajeError != null,
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

                Spacer(modifier = Modifier.height(14.dp))

                // Campo Contraseña
                Column(modifier = Modifier.fillMaxWidth()) {
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
                        TextButton(
                            onClick = { },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPrincipal
                            )
                        }
                    }
                    OutlinedTextField(
                        value = uiState.clave,
                        onValueChange = { loginViewModel.onClaveChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextoGris)
                        },
                        trailingIcon = {
                            IconButton(onClick = { loginViewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (uiState.isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = TextoGris
                                )
                            }
                        },
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = uiState.mensajeError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AzulPrincipal,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                // Mensaje de Error
                uiState.mensajeError?.let { texto ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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


                Spacer(modifier = Modifier.weight(1f, fill = false))
                Spacer(modifier = Modifier.height(32.dp))

                // Botón Iniciar Sesión
                Button(
                    onClick = { loginViewModel.iniciarSesion() },
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
            containerColor = if (seleccionado) Color.White else Color.Transparent,
            contentColor = if (seleccionado) TextoOscuro else TextoGris
        ),
        elevation = if (seleccionado) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
    ) {
        Icon(rol.icono, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(rol.etiqueta, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}