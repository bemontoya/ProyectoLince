package com.example.proyectolince.ui.viewmodel

import com.example.proyectolince.data.network.LinceApiService
import com.example.proyectolince.data.network.ServicioLinceDto
import com.example.proyectolince.data.repository.LinceRepository
import com.example.proyectolince.data.repository.ResultadoApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class LinceRepositoryTest {

    private lateinit var api: LinceApiService
    private lateinit var repository: LinceRepository

    @Before
    fun setup() {
        api = mockk()
        repository = LinceRepository(api)
    }

    @Test
    fun `obtenerServicios retorna Exito cuando API responde 200`() = runTest {
        val servicioMock = mockk<ServicioLinceDto>(relaxed = true)
        coEvery {
            api.getServicios(
                fechaDesde = any(),
                fechaHasta = any(),
                tipo = any(),
                estadoServicio = any(),
                page = any(),
                pageSize = any()
            )
        } returns Response.success(
            mockk(relaxed = true) {
                every { data } returns listOf(servicioMock)
            }
        )

        val res = repository.obtenerServicios()
        assertTrue(res is ResultadoApi.Exito)
        assertEquals(1, (res as ResultadoApi.Exito).datos.size)
    }
    @Test
    fun `obtenerServicios retorna Error con requestId cuando API responde 401`() = runTest {
        val errorBody = "No autorizado".toResponseBody("text/plain".toMediaType())
        val rawResponse = okhttp3.Response.Builder()
            .code(401)
            .message("Unauthorized")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost/").build())
            .headers(Headers.headersOf("x-request-id", "req-999"))
            .build()

        coEvery {
            api.getServicios(
                fechaDesde = any(),
                fechaHasta = any(),
                tipo = any(),
                estadoServicio = any(),
                page = any(),
                pageSize = any()
            )
        } returns Response.error(errorBody, rawResponse)

        val res = repository.obtenerServicios()
        assertTrue(res is ResultadoApi.Error)
        assertEquals(401, (res as ResultadoApi.Error).codigoHttp)
        assertEquals("req-999", res.requestId)
    }

    @Test
    fun `obtenerDetalleServicio rechaza ID no numerico sin llamar API`() = runTest {
        val res = repository.obtenerDetalleServicio("ABC")
        assertTrue(res is ResultadoApi.Error)
        assertTrue((res as ResultadoApi.Error).mensaje.contains("ID de servicio inválido"))
    }

    @Test
    fun `traducirEstado convierte codigos CF RQ WL XX correctamente`() {
        assertEquals("CONFIRMADO", repository.traducirEstado("CF"))
        assertEquals("PENDIENTE", repository.traducirEstado("RQ"))
        assertEquals("LISTA DE ESPERA", repository.traducirEstado("WL"))
        assertEquals("CANCELADO", repository.traducirEstado("XX"))
    }

    @Test
    fun `validarAsignacionRecurso verifica transportista o guia segun tipo`() {
        val transferSinDriver = mockk<ServicioLinceDto>(relaxed = true) {
            every { tipo } returns "TRANSFER"
            every { transportista } returns null
        }
        val guiadoConGuia = mockk<ServicioLinceDto>(relaxed = true) {
            every { tipo } returns "GUIDED_SERVICE"
            every { guia } returns mockk { every { id } returns 10 }
        }

        assertFalse(repository.validarAsignacionRecurso(transferSinDriver))
        assertTrue(repository.validarAsignacionRecurso(guiadoConGuia))
    }
}