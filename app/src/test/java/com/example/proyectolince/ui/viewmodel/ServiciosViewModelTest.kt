package com.example.proyectolince.ui.viewmodel

import com.example.proyectolince.data.EstadoServicio
import com.example.proyectolince.data.Servicio
import com.example.proyectolince.data.TipoServicio
import com.example.proyectolince.data.network.ServicioLinceDto
import com.example.proyectolince.data.repository.LinceRepository
import com.example.proyectolince.data.repository.ResultadoApi
import com.example.proyectolince.data.toServicioUi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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

@OptIn(ExperimentalCoroutinesApi::class)
class ServiciosViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: LinceRepository
    private lateinit var viewModel: ServiciosViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = ServiciosViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun crearListaMixtaDto(): List<ServicioLinceDto> {
        val dto1 = mockk<ServicioLinceDto>(relaxed = true)
        val dto2 = mockk<ServicioLinceDto>(relaxed = true)
        val dto3 = mockk<ServicioLinceDto>(relaxed = true)

        val s1 = Servicio(
            id = "1",
            referencia = "SRV-001",
            titulo = "Transfer Aeropuerto",
            tipo = TipoServicio.TRANSFER,
            fecha = "2026-11-20",
            hora = "09:00",
            pasajeros = 2,
            estado = EstadoServicio.PENDIENTE
        )
        val s2 = Servicio(
            id = "2",
            referencia = "SRV-002",
            titulo = "City Tour Santiago",
            tipo = TipoServicio.GUIADO,
            fecha = "2026-11-20",
            hora = "11:00",
            pasajeros = 4,
            estado = EstadoServicio.CONFIRMADO
        )
        val s3 = Servicio(
            id = "3",
            referencia = "SRV-003",
            titulo = "Excursión Cajón del Maipo",
            tipo = TipoServicio.EXCURSION,
            fecha = "2026-11-21",
            hora = "08:00",
            pasajeros = 6,
            estado = EstadoServicio.PENDIENTE
        )

        mockkStatic(ServicioLinceDto::toServicioUi)
        every { dto1.toServicioUi() } returns s1
        every { dto2.toServicioUi() } returns s2
        every { dto3.toServicioUi() } returns s3

        return listOf(dto1, dto2, dto3)
    }

    @Test
    fun `cargarServiciosPorRol filtra solo TRANSFER y EXCURSION para DRIVER`() = runTest {
        coEvery { repository.obtenerServicios(any(), any()) } returns ResultadoApi.Exito(crearListaMixtaDto())

        viewModel.cargarServiciosPorRol("DRIVER")
        advanceUntilIdle()

        val servicios = viewModel.uiState.value.servicios
        assertEquals(2, servicios.size)
        assertTrue(servicios.none { it.tipo == TipoServicio.GUIADO })
    }

    @Test
    fun `cargarServiciosPorRol filtra solo GUIADO y EXCURSION para GUIA`() = runTest {
        coEvery { repository.obtenerServicios(any(), any()) } returns ResultadoApi.Exito(crearListaMixtaDto())

        viewModel.cargarServiciosPorRol("GUIA")
        advanceUntilIdle()

        val servicios = viewModel.uiState.value.servicios
        assertEquals(2, servicios.size)
        assertTrue(servicios.none { it.tipo == TipoServicio.TRANSFER })
    }

    @Test
    fun `cargarServiciosPorRol muestra todos los servicios para COORDINADOR`() = runTest {
        coEvery { repository.obtenerServicios(any(), any()) } returns ResultadoApi.Exito(crearListaMixtaDto())

        viewModel.cargarServiciosPorRol("COORDINADOR")
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.servicios.size)
    }

    @Test
    fun `actualizarEstadoLocal cambia estado a CONFIRMADO o RECHAZADO con motivo`() = runTest {
        coEvery { repository.obtenerServicios(any(), any()) } returns ResultadoApi.Exito(crearListaMixtaDto())
        viewModel.cargarServiciosPorRol("COORDINADOR")
        advanceUntilIdle()

        viewModel.actualizarEstadoLocal("1", EstadoServicio.CONFIRMADO)
        viewModel.actualizarEstadoLocal("3", EstadoServicio.RECHAZADO, "Vehículo en mantención")

        val s1 = viewModel.uiState.value.servicios.first { it.id == "1" }
        val s3 = viewModel.uiState.value.servicios.first { it.id == "3" }

        assertEquals(EstadoServicio.CONFIRMADO, s1.estado)
        assertEquals(EstadoServicio.RECHAZADO, s3.estado)
        assertEquals("Vehículo en mantención", s3.motivoRechazo)
    }

    @Test
    fun `cargarServiciosPorRol propaga mensaje de error cuando falla API`() = runTest {
        coEvery { repository.obtenerServicios(any(), any()) } returns ResultadoApi.Error("Error HTTP 400")

        viewModel.cargarServiciosPorRol("DRIVER")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Error HTTP 400", viewModel.uiState.value.errorMessage)
    }
}