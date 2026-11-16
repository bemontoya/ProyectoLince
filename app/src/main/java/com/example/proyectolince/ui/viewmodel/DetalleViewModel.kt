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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.UUID

data class EstadoGpsGeocerca(
    val gpsActual: GpsSnapshot? = null,
    val geocercaDestino: GeocercaDto? = null,
    val distanciaMetros: Double? = null,
    val dentroDeRadio: Boolean = false,
    val llegadaVerificada: Boolean = false,
    val incidenciaDetectada: String? = null,
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

    companion object {
        const val RADIO_DEFAULT_METROS = 150.0
        const val PRECISION_MAXIMA_METROS = 50f
        const val ANTICIPACION_MINIMA_MINUTOS = 15L

        // Catálogo local de hoteles/pickups de respaldo (Anexo B: coordenadas de respaldo cuando pickup es texto)
        private val catalogoGeocercasFallback = listOf(
            GeocercaDto(1L, "Aeropuerto Arturo Merino Benítez", -33.3930, -70.7858, 150.0),
            GeocercaDto(2L, "Hotel W Santiago", -33.4161, -70.6022, 150.0),
            GeocercaDto(3L, "Hotel Mandarin Oriental", -33.4019, -70.5778, 150.0),
            GeocercaDto(4L, "Hotel Ritz-Carlton", -33.4152, -70.6003, 150.0),
            GeocercaDto(5L, "Hotel Sheraton Santiago", -33.4216, -70.6194, 150.0),
            GeocercaDto(6L, "Plaza de Armas Santiago", -33.4378, -70.6504, 150.0)
        )
    }

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
        var geocercaEncontrada: GeocercaDto? = null
        try {
            val bearer = sessionManager?.getBearerToken()
            if (bearer != null) {
                val resp = BackendRetrofitInstance.api.getGeocercas(bearer)
                if (resp.isSuccessful) {
                    geocercaEncontrada = resp.body()?.find {
                        it.nombreLugar.contains(nombreLugar.trim(), ignoreCase = true) ||
                                nombreLugar.trim().contains(it.nombreLugar, ignoreCase = true)
                    }
                }
            }
        } catch (_: Exception) {
        }

        // Respaldo catálogo local si el backend no está disponible o no tiene ese hotel
        if (geocercaEncontrada == null) {
            geocercaEncontrada = catalogoGeocercasFallback.find {
                it.nombreLugar.contains(nombreLugar.trim(), ignoreCase = true) ||
                        nombreLugar.trim().contains(it.nombreLugar, ignoreCase = true)
            } ?: GeocercaDto(0L, nombreLugar, -33.4378, -70.6504, RADIO_DEFAULT_METROS)
        }

        _estadoGps.value = _estadoGps.value.copy(geocercaDestino = geocercaEncontrada)
        recalcularDistancia()
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

    /**
     * Evalúa las 3 reglas operativas de Southbound (Anexo B):
     * 1. Precisión GPS <= 50 m
     * 2. Distancia <= radio de geocerca (150 m)
     * 3. Llegada con al menos 15 minutos de anticipación respecto a horaPickupProgramada
     */
    fun evaluarReglasCheckIn(
        gps: GpsSnapshot?,
        horaPickupProgramada: String,
        horaLlegada: LocalTime = LocalTime.now()
    ): Pair<Boolean, String> {
        if (gps == null) {
            return false to "INCIDENCIA: Sin señal GPS disponible"
        }
        if (gps.precisionMetros > PRECISION_MAXIMA_METROS) {
            return false to "INCIDENCIA: Precisión GPS insuficiente (±${gps.precisionMetros.toInt()}m > 50m)"
        }

        val geo = _estadoGps.value.geocercaDestino
        val radio = geo?.radioMetros ?: RADIO_DEFAULT_METROS
        val distancia = if (geo != null) {
            LocationHelper.calcularDistanciaHaversineMetros(
                gps.latitud, gps.longitud, geo.latitud, geo.longitud
            )
        } else {
            _estadoGps.value.distanciaMetros ?: 0.0
        }

        if (distancia > radio) {
            return false to "INCIDENCIA: Fuera de geocerca (${distancia.toInt()}m > ${radio.toInt()}m)"
        }

        val minutosAnticipacion = calcularMinutosAnticipacion(horaPickupProgramada, horaLlegada)
        if (minutosAnticipacion != null && minutosAnticipacion < ANTICIPACION_MINIMA_MINUTOS) {
            return false to if (minutosAnticipacion < 0) {
                "INCIDENCIA: Llegada con atraso (${-minutosAnticipacion} min tarde)"
            } else {
                "INCIDENCIA: Llegada con menos de 15 min de anticipación ($minutosAnticipacion min)"
            }
        }

        return true to "Llegada verificada en zona (${distancia.toInt()}m)"
    }

    internal fun calcularMinutosAnticipacion(
        horaPickupProgramada: String,
        horaLlegada: LocalTime
    ): Long? {
        val limpia = horaPickupProgramada.trim().take(5)
        return try {
            val pickup = LocalTime.parse(limpia, DateTimeFormatter.ofPattern("HH:mm"))
            ChronoUnit.MINUTES.between(horaLlegada, pickup)
        } catch (_: Exception) {
            null
        }
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
            val (verificada, detalleValidacion) = evaluarReglasCheckIn(gps, horaPickupProgramada)
            val incidencia = if (verificada) null else detalleValidacion

            _estadoGps.value = _estadoGps.value.copy(
                llegadaVerificada = verificada,
                incidenciaDetectada = incidencia,
                mensajeValidacion = detalleValidacion
            )

            val lat = gps?.latitud ?: 0.0
            val lon = gps?.longitud ?: 0.0
            val acc = gps?.precisionMetros ?: 999f
            val idemKey = "CI-${codigoServicio}-${UUID.randomUUID()}"

            val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val registroPrevio = repository.obtenerPorCodigo(codigoServicio)
            val obsConValidacion = listOfNotNull(registroPrevio?.observaciones, detalleValidacion)
                .joinToString(" | ")

            val nuevoRegistro = registroPrevio?.copy(
                horaCheckIn = horaActual,
                observaciones = obsConValidacion,
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
                observaciones = obsConValidacion,
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
                        val msg = body.observacionValidacion ?: detalleValidacion
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
                onResultado("$detalleValidacion (en cola offline)")
            } catch (_: Exception) {
                SyncOperacionesWorker.encolarSincronizacion(context)
                onResultado("$detalleValidacion (guardado offline)")
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
                        onResultado("Check-out sincronizado con servidor")
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