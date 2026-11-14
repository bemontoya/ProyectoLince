package com.example.proyectolince.data.sync

import android.content.Context
import androidx.work.*
import com.example.proyectolince.data.AppDatabase
import com.example.proyectolince.data.SessionManager
import com.example.proyectolince.data.network.BackendRetrofitInstance
import com.example.proyectolince.data.network.CheckInOutBackendRequest
import java.util.UUID
import java.util.concurrent.TimeUnit

class SyncOperacionesWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.registroDao()
        val sessionManager = SessionManager(applicationContext)

        val bearerToken = sessionManager.getBearerToken() ?: return Result.retry()
        val pendientes = dao.obtenerPendientesSincronizacion()

        if (pendientes.isEmpty()) return Result.success()

        var huboFalloRed = false

        for (registro in pendientes) {
            var checkInOk = true
            var checkOutOk = true

            // Sincronizar Check-In si existe
            if (!registro.horaCheckIn.isNullOrBlank()) {
                try {
                    val reqCheckIn = CheckInOutBackendRequest(
                        idempotencyKey = registro.idempotencyKeyCheckIn
                            ?: "CI-${registro.codigoServicio}-${UUID.randomUUID()}",
                        servicioId = registro.codigoServicio,
                        tipoEvento = "CHECK_IN",
                        latitud = registro.latitudCheckIn ?: 0.0,
                        longitud = registro.longitudCheckIn ?: 0.0,
                        precisionGpsMetros = registro.precisionCheckIn ?: 10f,
                        nombreLugar = registro.nombreLugarOrigen ?: "Punto de origen",
                        horaPickupProgramada = registro.horaPickupProgramada
                    )
                    val resp = BackendRetrofitInstance.api.registrarCheckInOut(bearerToken, reqCheckIn)
                    if (!resp.isSuccessful) {
                        checkInOk = false
                    }
                } catch (_: Exception) {
                    checkInOk = false
                    huboFalloRed = true
                }
            }

            // Sincronizar Check-Out si existe
            if (!registro.horaCheckOut.isNullOrBlank()) {
                try {
                    val reqCheckOut = CheckInOutBackendRequest(
                        idempotencyKey = registro.idempotencyKeyCheckOut
                            ?: "CO-${registro.codigoServicio}-${UUID.randomUUID()}",
                        servicioId = registro.codigoServicio,
                        tipoEvento = "CHECK_OUT",
                        latitud = registro.latitudCheckOut ?: 0.0,
                        longitud = registro.longitudCheckOut ?: 0.0,
                        precisionGpsMetros = registro.precisionCheckOut ?: 10f,
                        nombreLugar = registro.nombreLugarDestino ?: "Punto de destino"
                    )
                    val resp = BackendRetrofitInstance.api.registrarCheckInOut(bearerToken, reqCheckOut)
                    if (!resp.isSuccessful) {
                        checkOutOk = false
                    }
                } catch (_: Exception) {
                    checkOutOk = false
                    huboFalloRed = true
                }
            }

            if (checkInOk && checkOutOk) {
                dao.marcarComoSincronizado(registro.id)
            }
        }

        return if (huboFalloRed) Result.retry() else Result.success()
    }

    companion object {
        private const val WORK_NAME = "sync_operaciones_lince"

        fun encolarSincronizacion(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncOperacionesWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15,
                    TimeUnit.SECONDS
                )
                .build()

            WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                request
            )
        }
    }
}