package com.example.proyectolince.ui.viewmodel

import android.content.Context
import com.example.proyectolince.data.GpsSnapshot
import com.example.proyectolince.data.RegistroRepository
import com.example.proyectolince.data.SessionManager
import com.example.proyectolince.data.repository.LinceRepository
import com.example.proyectolince.data.repository.ResultadoApi
import com.example.proyectolince.data.sync.SyncOperacionesWorker
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: RegistroRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var linceRepository: LinceRepository
    private lateinit var viewModel: DetalleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        sessionManager = mockk(relaxed = true)
        linceRepository = mockk(relaxed = true)
        viewModel = DetalleViewModel(repository, sessionManager, linceRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `evaluarReglasCheckIn rechaza cuando gps es nulo`() {
        val (verificada, mensaje) = viewModel.evaluarReglasCheckIn(null, "10:00", LocalTime.of(9, 30))
        assertFalse(verificada)
        assertTrue(mensaje.contains("Sin señal GPS"))
    }

    @Test
    fun `evaluarReglasCheckIn rechaza cuando precision GPS supera 50 metros`() {
        val gpsImpreciso = GpsSnapshot(-33.4378, -70.6504, 75f)
        val (verificada, mensaje) = viewModel.evaluarReglasCheckIn(gpsImpreciso, "10:00", LocalTime.of(9, 30))
        assertFalse(verificada)
        assertTrue(mensaje.contains("Precisión GPS insuficiente"))
    }

    @Test
    fun `evaluarReglasCheckIn rechaza cuando ubicacion esta fuera de geocerca de 150m`() = runTest {
        coEvery { repository.obtenerPorCodigo("SRV-1") } returns null
        coEvery { linceRepository.obtenerDetalleServicio("SRV-1") } returns ResultadoApi.Exito(
            mockk(relaxed = true) { every { pickup } returns "Plaza de Armas Santiago" }
        )
        coEvery { sessionManager.getBearerToken() } returns null

        viewModel.cargarServicioYRegistro("SRV-1")
        advanceUntilIdle()

        // Coordenada a ~1.5 km de Plaza de Armas
        val gpsLejos = GpsSnapshot(-33.4500, -70.6504, 10f)
        val (verificada, mensaje) = viewModel.evaluarReglasCheckIn(gpsLejos, "10:00", LocalTime.of(9, 30))

        assertFalse(verificada)
        assertTrue(mensaje.contains("Fuera de geocerca"))
    }

    @Test
    fun `evaluarReglasCheckIn rechaza cuando faltan menos de 15 minutos para pickup`() = runTest {
        coEvery { repository.obtenerPorCodigo("SRV-2") } returns null
        coEvery { linceRepository.obtenerDetalleServicio("SRV-2") } returns ResultadoApi.Exito(
            mockk(relaxed = true) { every { pickup } returns "Plaza de Armas Santiago" }
        )
        coEvery { sessionManager.getBearerToken() } returns null

        viewModel.cargarServicioYRegistro("SRV-2")
        advanceUntilIdle()

        val gpsEnZona = GpsSnapshot(-33.4378, -70.6504, 8f)
        // Pickup 10:00, llegada 09:52 (solo 8 min antes)
        val (verificada, mensaje) = viewModel.evaluarReglasCheckIn(gpsEnZona, "10:00", LocalTime.of(9, 52))

        assertFalse(verificada)
        assertTrue(mensaje.contains("menos de 15 min"))
    }

    @Test
    fun `evaluarReglasCheckIn rechaza con incidencia cuando llega atrasado`() = runTest {
        coEvery { repository.obtenerPorCodigo("SRV-3") } returns null
        coEvery { linceRepository.obtenerDetalleServicio("SRV-3") } returns ResultadoApi.Exito(
            mockk(relaxed = true) { every { pickup } returns "Plaza de Armas Santiago" }
        )
        coEvery { sessionManager.getBearerToken() } returns null

        viewModel.cargarServicioYRegistro("SRV-3")
        advanceUntilIdle()

        val gpsEnZona = GpsSnapshot(-33.4378, -70.6504, 8f)
        val (verificada, mensaje) = viewModel.evaluarReglasCheckIn(gpsEnZona, "10:00", LocalTime.of(10, 10))

        assertFalse(verificada)
        assertTrue(mensaje.contains("atraso"))
    }

    @Test
    fun `evaluarReglasCheckIn aprueba dentro de radio y con mas de 15 minutos de anticipacion`() = runTest {
        coEvery { repository.obtenerPorCodigo("SRV-4") } returns null
        coEvery { linceRepository.obtenerDetalleServicio("SRV-4") } returns ResultadoApi.Exito(
            mockk(relaxed = true) { every { pickup } returns "Plaza de Armas Santiago" }
        )
        coEvery { sessionManager.getBearerToken() } returns null

        viewModel.cargarServicioYRegistro("SRV-4")
        advanceUntilIdle()

        val gpsEnZona = GpsSnapshot(-33.4379, -70.6504, 12f)
        val (verificada, mensaje) = viewModel.evaluarReglasCheckIn(gpsEnZona, "10:00", LocalTime.of(9, 40))

        assertTrue(verificada)
        assertTrue(mensaje.contains("Llegada verificada"))
    }

    @Test
    fun `registrarCheckInConGps guarda en Room y encola WorkManager cuando no hay red`() = runTest {
        val context = mockk<Context>(relaxed = true)
        mockkObject(SyncOperacionesWorker)
        every { SyncOperacionesWorker.encolarSincronizacion(any()) } just Runs
        coEvery { repository.obtenerPorCodigo("SRV-5") } returns null
        coEvery { sessionManager.getBearerToken() } returns null

        var callbackMsg = ""
        viewModel.registrarCheckInConGps(
            context = context,
            codigoServicio = "SRV-5",
            nombreLugar = "Hotel W Santiago",
            horaPickupProgramada = "23:59",
            gps = GpsSnapshot(-33.4161, -70.6022, 10f),
            onResultado = { callbackMsg = it }
        )
        advanceUntilIdle()

        coVerify { repository.guardarRegistro(any()) }
        verify { SyncOperacionesWorker.encolarSincronizacion(context) }
        assertTrue(callbackMsg.contains("offline"))
    }

    @Test
    fun `cargarServicioYRegistro actualiza errorServicio cuando API Lince retorna error`() = runTest {
        coEvery { repository.obtenerPorCodigo("SRV-ERR") } returns null
        coEvery { linceRepository.obtenerDetalleServicio("SRV-ERR") } returns ResultadoApi.Error("HTTP 401 No autorizado")

        viewModel.cargarServicioYRegistro("SRV-ERR")
        advanceUntilIdle()

        assertEquals("HTTP 401 No autorizado", viewModel.errorServicio.value)
    }
}