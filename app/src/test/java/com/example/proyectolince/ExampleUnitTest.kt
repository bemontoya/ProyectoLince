package com.example.proyectolince

import com.example.proyectolince.data.LocationHelper
import com.example.proyectolince.data.network.ServicioLinceDto
import com.example.proyectolince.data.repository.LinceRepository
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    private val repository = LinceRepository()
    private val gson = Gson()

    @Test
    fun distanciaHaversine_dentroDeGeocerca150m_esValida() {
        val latOrigen = -41.3186
        val lonOrigen = -72.9781

        val latChofer = -41.3189
        val lonChofer = -72.9784

        val distancia = LocationHelper.calcularDistanciaHaversineMetros(
            latOrigen, lonOrigen, latChofer, lonChofer
        )

        assertTrue("Distancia ($distancia m) debe ser <= 150 m", distancia <= 150.0)
    }

    @Test
    fun distanciaHaversine_fueraDeGeocerca150m_superaElRadio() {
        val latOrigen = -41.3186
        val lonOrigen = -72.9781

        val latLejos = -41.4389
        val lonLejos = -73.0940

        val distancia = LocationHelper.calcularDistanciaHaversineMetros(
            latOrigen, lonOrigen, latLejos, lonLejos
        )

        assertTrue("Distancia ($distancia m) debe superar los 150 m", distancia > 150.0)
    }

    @Test
    fun validarAsignacionRecurso_cumpleReglasDeNegocioLince() {
        val jsonTransferValido = """
            {
              "id": "SRV-001",
              "codigo": "SRV-001",
              "tipo": "TRANSFER",
              "estado": "CF",
              "transportista": { "id": "TRP-01", "nombre": "Juan Pérez" },
              "guia": null
            }
        """.trimIndent()

        val jsonGuiadoSinGuia = """
            {
              "id": "SRV-002",
              "codigo": "SRV-002",
              "tipo": "GUIDED_SERVICE",
              "estado": "CF",
              "transportista": { "id": "TRP-01", "nombre": "Juan Pérez" },
              "guia": null
            }
        """.trimIndent()

        val transferValido = gson.fromJson(jsonTransferValido, ServicioLinceDto::class.java)
        val guiadoSinGuia = gson.fromJson(jsonGuiadoSinGuia, ServicioLinceDto::class.java)

        assertTrue(repository.validarAsignacionRecurso(transferValido))
        assertFalse(repository.validarAsignacionRecurso(guiadoSinGuia))
    }

    @Test
    fun traducirEstado_convierteCodigosLinceCorrectamente() {
        assertEquals("CONFIRMADO", repository.traducirEstado("CF"))
        assertEquals("PENDIENTE", repository.traducirEstado("RQ"))
        assertEquals("LISTA DE ESPERA", repository.traducirEstado("WL"))
        assertEquals("CANCELADO", repository.traducirEstado("XX"))
    }
}