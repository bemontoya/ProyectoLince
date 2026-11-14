package com.example.proyectolince.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolince.data.*
import com.example.proyectolince.data.network.BackendRetrofitInstance
import com.example.proyectolince.data.network.CheckInOutBackendRequest
import com.example.proyectolince.data.network.GeocercaDto
import com.example.proyectolince.data.network.ServicioLinceDto
import com.example.proyectolince.data.repository.LinceRepository
import com.example.proyectolince.data.repository.ResultadoApi
import com.example.proyectolince.data.sync.SyncOperacionesWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class EstadoGpsGeocerca(
    val gpsActual: GpsSnapshot? = null,
    val geocercaDestino: GeocercaDto? = null,
    val distanciaMetros: Double? = null,
    val dentroDeRadio: Boolean = false,
    val mensajeValidacion: String? = null,
    val sincronizadoBackend: Boolean = false
)

class DetalleViewModel(
    private val repository: RegistroRepository,
    private val sessionManager: SessionManager? = null,
    private val linceRepository: LinceRepository = LinceRepository()
) : ViewModel() {

    private val _servicio = MutableStateFlow<ServicioLinceDto?>(null)
    val servicio: StateFlow<ServicioLinceDto?> = _servicio.asStateFlow()

    private val _errorServicio = MutableStateFlow<String?>(null)
    val errorServicio: StateFlow<String?> = _errorServicio.asStateFlow()

    private val _registroActual = MutableStateFlow<RegistroOperacional?>(null)
    val registroActual: StateFlow<RegistroOperacional?> = _registroActual.asStateFlow()

    private val _estadoGps = MutableStateFlow(EstadoGpsGeocerca())
    val estadoGps: StateFlow<EstadoGpsGeocerca> = _estadoGps.asStateFlow()

    fun cargarServicioYRegistro(servicioId: String) {
        viewModelScope.launch {
            _registroActual.value = repository.obtenerPorCodigo(servicioId)

            when (val res = linceRepository.obtenerDetalleServicio(servicioId)) {
                is ResultadoApi.Exito -> {
                    _servicio.value = res.datos
                    _errorServicio.value = null
                    res.datos.pickup?.let { cargarGeocercaLugar(it) }
                }
                is ResultadoApi.Error -> {
                    _errorServicio.value = res.mensaje
                }
            }
        }
    }

    private suspend fun cargarGeocercaLugar(nombreLugar: String) {
        try {
            val bearer = sessionManager?.getBearerToken() ?: return
            val resp = BackendRetrofitInstance.api.getGeocercas(bearer)
            if (resp.isSuccessful) {
                val match = resp.body()?.find {
                    it.nombreLugar.equals(nombreLugar.trim(), ignoreCase = true)
                }
                if (match != null) {
                    _estadoGps.value = _estadoGps.value.copy(geocercaDestino = match)
                    recalcularDistancia()
                }
            }
        } catch (_: Exception) {
        }
    }

    fun actualizarPosicionGps(snapshot: GpsSnapshot?) {
        _estadoGps.value = _estadoGps.value.copy(gpsActual = snapshot)
        recalcularDistancia()
    }

    private fun recalcularDistancia() {
        val gps = _estadoGps.value.gpsActual ?: return
        val geo = _estadoGps.value.geocercaDestino ?: return
        val dist = LocationHelper.calcularDistanciaHaversineMetros(
            gps.latitud, gps.longitud, geo.latitud, geo.longitud
        )
        _estadoGps.value = _estadoGps.value.copy(
            distanciaMetros = dist,
            dentroDeRadio = dist <= geo.radioMetros
        )
    }

    fun registrarCheckInConGps(
        context: Context,
        codigoServicio: String,
        nombreLugar: String,
        horaPickupProgramada: String,
        gps: GpsSnapshot?,
        onResultado: (String) -> Unit
    ) {
        viewModelScope.launch {
            val lat = gps?.latitud ?: 0.0
            val lon = gps?.longitud ?: 0.0
            val acc = gps?.precisionMetros ?: 10f
            val idemKey = "CI-${codigoServicio}-${UUID.randomUUID()}"

            val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val registroPrevio = repository.obtenerPorCodigo(codigoServicio)
            val nuevoRegistro = registroPrevio?.copy(
                horaCheckIn = horaActual,
                sincronizado = false,
                latitudCheckIn = lat,
                longitudCheckIn = lon,
                precisionCheckIn = acc,
                nombreLugarOrigen = nombreLugar,
                horaPickupProgramada = horaPickupProgramada,
                idempotencyKeyCheckIn = idemKey
            ) ?: RegistroOperacional(
                codigoServicio = codigoServicio,
                horaCheckIn = horaActual,
                horaCheckOut = null,
                observaciones = null,
                sincronizado = false,
                latitudCheckIn = lat,
                longitudCheckIn = lon,
                precisionCheckIn = acc,
                nombreLugarOrigen = nombreLugar,
                horaPickupProgramada = horaPickupProgramada,
                idempotencyKeyCheckIn = idemKey
            )
            repository.guardarRegistro(nuevoRegistro)
            val guardado = repository.obtenerPorCodigo(codigoServicio)
            _registroActual.value = guardado

            try {
                val bearer = sessionManager?.getBearerToken()
                if (bearer != null) {
                    val req = CheckInOutBackendRequest(
                        idempotencyKey = idemKey,
                        servicioId = codigoServicio,
                        tipoEvento = "CHECK_IN",
                        latitud = lat,
                        longitud = lon,
                        precisionGpsMetros = acc,
                        nombreLugar = nombreLugar,
                        horaPickupProgramada = horaPickupProgramada
                    )
                    val resp = BackendRetrofitInstance.api.registrarCheckInOut(bearer, req)
                    if (resp.isSuccessful && resp.body() != null) {
                        guardado?.let { repository.marcarComoSincronizado(it.id) }
                        _registroActual.value = guardado?.copy(sincronizado = true)
                        val body = resp.body()!!
                        val msg = body.observacionValidacion ?: "Check-in sincronizado con servidor"
                        _estadoGps.value = _estadoGps.value.copy(
                            mensajeValidacion = msg,
                            sincronizadoBackend = true,
                            distanciaMetros = body.distanciaGeocercaMetros ?: _estadoGps.value.distanciaMetros
                        )
                        onResultado(msg)
                        return@launch
                    }
                }
                SyncOperacionesWorker.encolarSincronizacion(context)
                onResultado("Sin red: Check-in en cola offline (WorkManager)")
            } catch (_: Exception) {
                SyncOperacionesWorker.encolarSincronizacion(context)
                onResultado("Modo offline: Check-in guardado en Room y encolado")
            }
        }
    }

    fun registrarCheckOutConGps(
        context: Context,
        codigoServicio: String,
        nombreLugarDestino: String,
        observaciones: String,
        gps: GpsSnapshot?,
        onResultado: (String) -> Unit
    ) {
        viewModelScope.launch {
            val lat = gps?.latitud ?: 0.0
            val lon = gps?.longitud ?: 0.0
            val acc = gps?.precisionMetros ?: 10f
            val idemKey = "CO-${codigoServicio}-${UUID.randomUUID()}"

            val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val registroPrevio = repository.obtenerPorCodigo(codigoServicio)
            val nuevoRegistro = registroPrevio?.copy(
                horaCheckOut = horaActual,
                observaciones = observaciones,
                sincronizado = false,
                latitudCheckOut = lat,
                longitudCheckOut = lon,
                precisionCheckOut = acc,
                nombreLugarDestino = nombreLugarDestino,
                idempotencyKeyCheckOut = idemKey
            ) ?: RegistroOperacional(
                codigoServicio = codigoServicio,
                horaCheckIn = null,
                horaCheckOut = horaActual,
                observaciones = observaciones,
                sincronizado = false,
                latitudCheckOut = lat,
                longitudCheckOut = lon,
                precisionCheckOut = acc,
                nombreLugarDestino = nombreLugarDestino,
                idempotencyKeyCheckOut = idemKey
            )
            repository.guardarRegistro(nuevoRegistro)
            val guardado = repository.obtenerPorCodigo(codigoServicio)
            _registroActual.value = guardado

            try {
                val bearer = sessionManager?.getBearerToken()
                if (bearer != null) {
                    val req = CheckInOutBackendRequest(
                        idempotencyKey = idemKey,
                        servicioId = codigoServicio,
                        tipoEvento = "CHECK_OUT",
                        latitud = lat,
                        longitud = lon,
                        precisionGpsMetros = acc,
                        nombreLugar = nombreLugarDestino
                    )
                    val resp = BackendRetrofitInstance.api.registrarCheckInOut(bearer, req)
                    if (resp.isSuccessful) {
                        guardado?.let { repository.marcarComoSincronizado(it.id) }
                        _registroActual.value = guardado?.copy(sincronizado = true)
                        _estadoGps.value = _estadoGps.value.copy(
                            mensajeValidacion = "Check-out sincronizado con servidor",
                            sincronizadoBackend = true
                        )
                        onResultado("Check-out sincronizado con servidor Lince")
                        return@launch
                    }
                }
                SyncOperacionesWorker.encolarSincronizacion(context)
                onResultado("Sin red: Check-out en cola offline (WorkManager)")
            } catch (_: Exception) {
                SyncOperacionesWorker.encolarSincronizacion(context)
                onResultado("Modo offline: Check-out guardado en Room y encolado")
            }
        }
    }
}