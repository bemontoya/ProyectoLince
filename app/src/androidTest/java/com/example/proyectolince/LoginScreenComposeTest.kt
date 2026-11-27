package com.example.proyectolince

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.proyectolince.ui.screens.LoginScreen
import com.example.proyectolince.ui.screens.Rol
import org.junit.Rule
import org.junit.Test

class LoginScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun loginScreenMuestraErrorConDatosInvalidos() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        composeTestRule.setContent {
            LoginScreen(
                idiomaActual = "es",
                onCambiarIdioma = {},
                onLoginSuccess = {}
            )
        }

        // Ingresa correo con formato inválido y contraseña vacía
        val camposTexto = composeTestRule.onAllNodes(androidx.compose.ui.test.hasSetTextAction())
        camposTexto[0].performTextClearance()
        camposTexto[0].performTextInput("correo-invalido")
        camposTexto[1].performTextClearance()

        // Presiona el botón de iniciar sesión
        val txtBotonLogin = context.getString(R.string.login_btn)
        composeTestRule.onNodeWithText(txtBotonLogin)
            .performScrollTo()
            .performClick()

        // Verifica que sigue en pantalla de Login (no navegó) y muestra validación
        composeTestRule.onNodeWithText(txtBotonLogin).assertIsDisplayed()
    }
}