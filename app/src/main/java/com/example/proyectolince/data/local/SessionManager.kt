package com.southbound.proyectolince.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("lince_session_prefs", Context.MODE_PRIVATE)

    companion object {
        private val _idiomaFlow = MutableStateFlow("es")
        val idiomaFlow: StateFlow<String> = _idiomaFlow.asStateFlow()
    }

    init {
        val guardado = prefs.getString("app_idioma", "es") ?: "es"
        if (_idiomaFlow.value != guardado) {
            _idiomaFlow.value = guardado
        }
    }

    fun setIdioma(codigo: String) {
        val normalizado = if (codigo.lowercase().startsWith("en")) "en" else "es"
        prefs.edit().putString("app_idioma", normalizado).apply()
        _idiomaFlow.value = normalizado
    }

    fun getIdioma(): String = prefs.getString("app_idioma", "es") ?: "es"

    fun saveSession(
        token: String,
        email: String,
        nombre: String,
        rol: String,
        linceRecursoId: Int?,
        oficinaCodigo: String?
    ) {
        prefs.edit().apply {
            putString("jwt_token", token)
            putString("user_email", email)
            putString("user_nombre", nombre)
            putString("user_rol", rol)
            if (linceRecursoId != null) putInt("lince_recurso_id", linceRecursoId)
            else remove("lince_recurso_id")
            putString("oficina_codigo", oficinaCodigo)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)
    fun getBearerToken(): String? = getToken()?.let { "Bearer $it" }
    fun getRol(): String? = prefs.getString("user_rol", null)
    fun getNombre(): String? = prefs.getString("user_nombre", null)
    fun getEmail(): String? = prefs.getString("user_email", null)
    fun getTelefono(): String = prefs.getString("user_telefono", null) ?: "+56 9 8765 4321"
    fun getTurno(): String = prefs.getString("user_turno", null) ?: "Mañana (06:00 - 14:00)"
    fun getLinceRecursoId(): Int? =
        if (prefs.contains("lince_recurso_id")) prefs.getInt("lince_recurso_id", -1) else null

    fun actualizarPerfil(nombre: String, telefono: String, turno: String) {
        prefs.edit().apply {
            putString("user_nombre", nombre)
            putString("user_telefono", telefono)
            putString("user_turno", turno)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clearSession() {
        val idiomaActual = getIdioma()
        prefs.edit().clear().putString("app_idioma", idiomaActual).apply()
    }
}