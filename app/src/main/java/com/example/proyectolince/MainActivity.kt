package com.example.proyectolince

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import com.example.proyectolince.navigation.AppNavigation
import com.example.proyectolince.ui.theme.ProyectoLinceTheme
import com.southbound.proyectolince.data.local.SessionManager
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)

        setContent {
            val idiomaActual by SessionManager.idiomaFlow.collectAsState()
            val cleanCode = if (idiomaActual.lowercase().startsWith("en")) "en" else "es"
            val baseConfig = LocalConfiguration.current

            val localizedConfig = remember(cleanCode, baseConfig) {
                val locale = Locale.forLanguageTag(cleanCode)
                Locale.setDefault(locale)
                Configuration(baseConfig).apply {
                    setLocale(locale)
                    setLayoutDirection(locale)
                }
            }

            @Suppress("DEPRECATION")
            resources.updateConfiguration(localizedConfig, resources.displayMetrics)

            key(cleanCode) {
                CompositionLocalProvider(LocalConfiguration provides localizedConfig) {
                    ProyectoLinceTheme {
                        AppNavigation()
                    }
                }
            }
        }
    }
}