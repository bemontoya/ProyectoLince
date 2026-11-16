package com.example.proyectolince

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.proyectolince.ui.screens.LoginScreen
import com.example.proyectolince.ui.screens.Rol
import org.junit.Rule
import org.junit.Test

class LoginScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreenMuestraRolesCamposYPermiteInteraccion() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        var rolSeleccionado: Rol? = null

        composeTestRule.setContent {
            LoginScreen(
                idiomaActual = "es",
                onCambiarIdioma = {},
                onLoginSuccess = { rolSeleccionado = it }
            )
        }

        val tituloApp = context.getString(R.string.subtitulo_app)
        val txtDriver = context.getString(R.string.rol_driver)
        val txtGuia = context.getString(R.string.rol_guia)
        val txtCoordinador = context.getString(R.string.rol_coordinador)
        val txtBotonLogin = context.getString(R.string.login_btn)

        // Verifica título y pestañas de rol
        composeTestRule.onNodeWithText(tituloApp).assertIsDisplayed()
        composeTestRule.onNodeWithText(txtDriver).assertIsDisplayed()
        composeTestRule.onNodeWithText(txtGuia).assertIsDisplayed()
        composeTestRule.onNodeWithText(txtCoordinador).assertIsDisplayed()

        // Cambia a rol Guía
        composeTestRule.onNodeWithText(txtGuia).performClick()

        // Hace scroll hasta el botón de acceso y verifica que esté visible
        composeTestRule.onNodeWithText(txtBotonLogin)
            .performScrollTo()
            .assertIsDisplayed()
    }
}