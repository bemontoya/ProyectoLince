package com.example.proyectolince.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.*

data class GpsSnapshot(
    val latitud: Double,
    val longitud: Double,
    val precisionMetros: Float
)

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacionActual(context: Context): GpsSnapshot? {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        return suspendCoroutine { cont ->
            val cts = CancellationTokenSource()
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        cont.resume(
                            GpsSnapshot(
                                latitud = location.latitude,
                                longitud = location.longitude,
                                precisionMetros = location.accuracy
                            )
                        )
                    } else {
                        fusedClient.lastLocation
                            .addOnSuccessListener { last ->
                                if (last != null) {
                                    cont.resume(GpsSnapshot(last.latitude, last.longitude, last.accuracy))
                                } else {
                                    cont.resume(null)
                                }
                            }
                            .addOnFailureListener { cont.resume(null) }
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }

    fun calcularDistanciaHaversineMetros(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}