package com.example.proyectolince.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun ProveedorIdioma(
    codigoIdioma: String,
    content: @Composable () -> Unit
) {
    val contextOriginal = LocalContext.current
    val baseConfig = LocalConfiguration.current
    val cleanCode = if (codigoIdioma.lowercase().startsWith("en")) "en" else "es"

    val nuevaConfig = remember(cleanCode, baseConfig) {
        val locale = Locale.forLanguageTag(cleanCode)
        Locale.setDefault(locale)

        Configuration(baseConfig).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }

    @Suppress("DEPRECATION")
    contextOriginal.resources.updateConfiguration(nuevaConfig, contextOriginal.resources.displayMetrics)

    key(cleanCode) {
        CompositionLocalProvider(
            LocalConfiguration provides nuevaConfig
        ) {
            content()
        }
    }
}