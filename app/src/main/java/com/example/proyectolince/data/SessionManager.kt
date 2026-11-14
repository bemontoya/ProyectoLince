package com.example.proyectolince.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "lince_session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_EMAIL = stringPreferencesKey("user_email")
        private val KEY_NOMBRE = stringPreferencesKey("user_nombre")
        private val KEY_ROL = stringPreferencesKey("user_rol")
        private val KEY_RECURSO_ID = stringPreferencesKey("user_recurso_id")
        private val KEY_IDIOMA = stringPreferencesKey("app_language")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val nombreFlow: Flow<String> = context.dataStore.data.map { it[KEY_NOMBRE] ?: "Operador" }
    val rolFlow: Flow<String> = context.dataStore.data.map { it[KEY_ROL] ?: "DRIVER" }
    val recursoIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_RECURSO_ID] ?: inferirRecursoPorEmail(prefs[KEY_EMAIL])
    }
    val idiomaFlow: Flow<String> = context.dataStore.data.map { it[KEY_IDIOMA] ?: "es" }

    suspend fun saveSession(
        token: String,
        email: String,
        nombre: String,
        rol: String,
        recursoId: String? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_EMAIL] = email
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_ROL] = rol
            val idFinal = recursoId ?: inferirRecursoPorEmail(email)
            if (idFinal != null) {
                prefs[KEY_RECURSO_ID] = idFinal
            } else {
                prefs.remove(KEY_RECURSO_ID)
            }
        }
    }

    private fun inferirRecursoPorEmail(email: String?): String? {
        return when (email?.lowercase()?.trim()) {
            "driver@southbound.cl", "driver@lince.cl" -> "TRP-01"
            "guia@southbound.cl", "guia@lince.cl" -> "GUI-01"
            else -> null
        }
    }

    suspend fun getBearerToken(): String? {
        val token = tokenFlow.first()
        return if (!token.isNullOrBlank()) "Bearer $token" else null
    }

    suspend fun saveLanguage(langCode: String) {
        context.dataStore.edit { it[KEY_IDIOMA] = langCode }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_NOMBRE)
            prefs.remove(KEY_ROL)
            prefs.remove(KEY_RECURSO_ID)
        }
    }
}