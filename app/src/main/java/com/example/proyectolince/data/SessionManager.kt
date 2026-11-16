package com.example.proyectolince.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        private val KEY_LINCE_RECURSO_ID = intPreferencesKey("lince_recurso_id")
        private val KEY_OFICINA = stringPreferencesKey("oficina_codigo")
        private val KEY_IDIOMA = stringPreferencesKey("app_language")
        private val KEY_TELEFONO = stringPreferencesKey("user_telefono")
        private val KEY_TURNO = stringPreferencesKey("user_turno")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val emailFlow: Flow<String> = context.dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val nombreFlow: Flow<String> = context.dataStore.data.map { it[KEY_NOMBRE] ?: "Operador" }
    val rolFlow: Flow<String> = context.dataStore.data.map { it[KEY_ROL] ?: "DRIVER" }
    val linceRecursoIdFlow: Flow<Int?> = context.dataStore.data.map { it[KEY_LINCE_RECURSO_ID] }
    val oficinaCodigoFlow: Flow<String?> = context.dataStore.data.map { it[KEY_OFICINA] }
    val recursoIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_RECURSO_ID] ?: inferirRecursoPorEmail(prefs[KEY_EMAIL])
    }
    val idiomaFlow: Flow<String> = context.dataStore.data.map { it[KEY_IDIOMA] ?: "es" }
    val telefonoFlow: Flow<String> = context.dataStore.data.map { it[KEY_TELEFONO] ?: "+56 9 8765 4321" }
    val turnoFlow: Flow<String> = context.dataStore.data.map { it[KEY_TURNO] ?: "Mañana (06:00 - 14:00)" }

    suspend fun saveSession(
        token: String,
        email: String,
        nombre: String,
        rol: String,
        linceRecursoId: Int? = null,
        oficinaCodigo: String? = null,
        recursoId: String? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_EMAIL] = email
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_ROL] = rol

            if (linceRecursoId != null) {
                prefs[KEY_LINCE_RECURSO_ID] = linceRecursoId
            } else {
                prefs.remove(KEY_LINCE_RECURSO_ID)
            }

            if (oficinaCodigo != null) {
                prefs[KEY_OFICINA] = oficinaCodigo
            } else {
                prefs.remove(KEY_OFICINA)
            }

            val idFinal = recursoId ?: inferirRecursoPorEmail(email)
            if (idFinal != null) {
                prefs[KEY_RECURSO_ID] = idFinal
            } else {
                prefs.remove(KEY_RECURSO_ID)
            }
        }
    }

    suspend fun actualizarPerfil(nombre: String, telefono: String, turno: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_TELEFONO] = telefono
            prefs[KEY_TURNO] = turno
        }
    }

    private fun inferirRecursoPorEmail(email: String?): String? {
        return when (email?.lowercase()?.trim()) {
            "driver@southbound.cl", "driver@lince.cl", "driver01@demo.lince" -> "TRP-01"
            "guia@southbound.cl", "guia@lince.cl", "guia01@demo.lince" -> "GUI-01"
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
            prefs.remove(KEY_LINCE_RECURSO_ID)
            prefs.remove(KEY_OFICINA)
            prefs.remove(KEY_TELEFONO)
            prefs.remove(KEY_TURNO)
        }
    }
}