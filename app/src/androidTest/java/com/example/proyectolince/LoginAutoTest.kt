package com.example.proyectolince

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.ui.test.hasSetTextAction

@RunWith(AndroidJUnit4::class)
class LoginAutoTest {

    // Inicia automáticamente la actividad principal en la app
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun flujoLoginAutomatico_driverIniciaSesionYEntraAServicios() {
        // Espera a que cargue la pantalla de Login
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Iniciar Sesión", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Seleccionar el botón de rol "Driver"
        val nodosDriver = composeTestRule.onAllNodesWithText("Driver", ignoreCase = true)
            .fetchSemanticsNodes()
        if (nodosDriver.isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Driver", ignoreCase = true)[0].performClick()
        }

        // Escribe automáticamente el correo en el campo de Correo

        val campoCorreo = composeTestRule.onAllNodes(hasSetTextAction())[0]
        campoCorreo.performTextClearance()
        campoCorreo.performTextInput("driver01@demo.lince")


        val campoPassword = composeTestRule.onAllNodes(hasSetTextAction())[1]
        campoPassword.performTextClearance()
        campoPassword.performTextInput("Lince2026")


        // Hace clic automático en el botón de iniciar sesión
        val botonesEntrar = composeTestRule.onAllNodesWithText("Iniciar Sesión", substring = true, ignoreCase = true)
        botonesEntrar[botonesEntrar.fetchSemanticsNodes().size - 1].performClick()

        // Esperaa la respuesta real de InsForge y verificar que navegó a "Servicios"
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule.onAllNodesWithText("Servicios", substring = true, ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("Servicios", substring = true, ignoreCase = true)[0]
            .assertIsDisplayed()
    }
}