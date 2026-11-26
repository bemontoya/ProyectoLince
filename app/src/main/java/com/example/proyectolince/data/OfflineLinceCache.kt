package com.example.proyectolince.data

import android.content.Context
import com.example.proyectolince.data.network.ServicioLinceDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object OfflineLinceCache {
    private const val PREFS_NAME = "lince_api_offline_cache"
    private val gson = Gson()

    fun guardarServicios(context: Context, lista: List<ServicioLinceDto>) {
        if (lista.isEmpty()) return
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("lista_servicios_dto", gson.toJson(lista)).apply()
        //  guarda cada uno por ID para abrir DetalleScreen sin internet
        lista.forEach { guardarDetalle(context, it) }
    }

    fun obtenerServicios(context: Context): List<ServicioLinceDto> {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("lista_servicios_dto", null) ?: return emptyList()
        val type = object : TypeToken<List<ServicioLinceDto>>() {}.type
        return runCatching { gson.fromJson<List<ServicioLinceDto>>(json, type) }.getOrNull().orEmpty()
    }

    fun guardarDetalle(context: Context, dto: ServicioLinceDto) {
        val id = dto.id ?: return
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("detalle_$id", gson.toJson(dto)).apply()
    }

    fun obtenerDetalle(context: Context, servicioId: String): ServicioLinceDto? {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("detalle_$servicioId", null) ?: return null
        return runCatching { gson.fromJson(json, ServicioLinceDto::class.java) }.getOrNull()
    }
}