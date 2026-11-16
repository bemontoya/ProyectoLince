package com.example.proyectolince.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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

    val nuevaConfig = remember(codigoIdioma, contextOriginal) {
        val locale = Locale.forLanguageTag(codigoIdioma)
        Locale.setDefault(locale)
        Configuration(contextOriginal.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }

    @Suppress("DEPRECATION")
    contextOriginal.resources.updateConfiguration(nuevaConfig, contextOriginal.resources.displayMetrics)

    CompositionLocalProvider(
        LocalConfiguration provides nuevaConfig,
        LocalContext provides contextOriginal
    ) {
        content()
    }
}