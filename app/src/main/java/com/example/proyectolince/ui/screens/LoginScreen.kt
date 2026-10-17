package com.example.proyectolince.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Reemplaza com.example.proyectolince con el paquete de tu proyecto si difiere
import com.example.proyectolince.R
//se importa toda la paleta de color con colores estandar definidos
import com.example.proyectolince.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}
) {

    //estados de la pantalla (variables cambian cuando el usuario interactúa)
    var rolSeleccionado by remember { mutableStateOf(0) } // 0: driver, 1: guia, 2: coordinador
    var usuarioText by remember { mutableStateOf("matias.jeldres@southbound.cl") }
    var passwordText by remember { mutableStateOf("123456789012") }
    var passwordVisible by remember { mutableStateOf(false) } //controla si la clave se ve o se ven puntos

    // Usamos Scaffold nativo para garantizar que tome los márgenes reales del teléfono
    Scaffold(
        containerColor = FondoPantalla
    ) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Distribuye el espacio de forma proporcional a todo el alto
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                // Contenedor e imagen del logo oficial
                Image(
                    painter = painterResource(id = R.drawable.logo_lince),
                    contentDescription = "Logo Southbound Field",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                //titulo principal
                Text(
                    text = "Southbound Field",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                Spacer(modifier = Modifier.height(6.dp))

                //subtitulo con punto verde
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(VerdeConfirmado, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Operaciones & Asignaciones Southbound",
                        fontSize = 13.sp,
                        color = TextoGris,
                        fontWeight = FontWeight.Medium
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                //selectro del rol
                Surface(
                    color = Color(0xFFE2E8F0).copy(alpha = 0.6f), //fondo gris claro
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        //pestaña de driver
                        Button(
                            onClick = { rolSeleccionado = 0 },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues (horizontal = 4.dp, vertical = 0.dp ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (rolSeleccionado == 0) Color.White else Color.Transparent,
                                contentColor = if (rolSeleccionado == 0) TextoOscuro else TextoGris
                            ),
                            elevation = if (rolSeleccionado == 0) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ){
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Driver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        //pestaña de guia
                        Button(
                            onClick = { rolSeleccionado = 1 },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (rolSeleccionado == 1) Color.White else Color.Transparent,
                                contentColor = if (rolSeleccionado == 1) TextoOscuro else TextoGris
                            ),
                            elevation = if (rolSeleccionado == 1) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center

                            ){
                            Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Guía", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        }

                        //pestaña coordinador
                        Button(
                            onClick = { rolSeleccionado = 2 },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (rolSeleccionado == 2) Color.White else Color.Transparent,
                                contentColor = if (rolSeleccionado == 2) TextoOscuro else TextoGris
                            ),
                            elevation = if (rolSeleccionado == 2) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            )
                            {
                            Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Coordinador", fontSize = 11.sp, fontWeight = FontWeight.Bold)}
                        }
                    }
                }

                //campo correo
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
                        onValueChange = { usuarioText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = TextoGris)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AzulPrincipal,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                //campo contraseña con la opcion de "¿olvidaste tu contraseña?"
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
                        onValueChange = { passwordText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextoGris)
                        },
                        //icono al final para mostrar u ocultaer la contraseña mediante el estado 'passwordVisible'
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Ocultar/Mostrar clave",
                                    tint = TextoGris
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AzulPrincipal,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                //boton de iniciar sesion
                Button(
                    onClick = {
                        println("DEBUG_LOG: Botón de Inciar Sesión presionado")
                        onLoginSuccess() },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            //pie de la pagina (Ubicado en el fondo nativo de la pantalla)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Southbound Expeditions & Transport Chile",
                    fontSize = 11.sp,
                    color = TextoGris
                )
            }
        }
    }
}