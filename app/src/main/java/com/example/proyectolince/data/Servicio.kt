package com.example.proyectolince.data

import androidx.annotation.DrawableRes
import com.example.proyectolince.R
import com.example.proyectolince.data.network.ServicioLinceDto

enum class TipoServicio(val etiqueta: String) {
    TRANSFER("TRANSFER"),
    EXCURSION("EXCURSIÓN"),
    GUIADO("GUIADO")
}

enum class EstadoServicio(val etiqueta: String) {
    PENDIENTE("PENDIENTE"),
    CONFIRMADO("CONFIRMADO"),
    RECHAZADO("RECHAZADO"),
    MODIFICADO("MODIFICADO"),
    FINALIZADO("FINALIZADO")
}

data class Servicio(
    val id: String,
    val tipo: TipoServicio,
    val estado: EstadoServicio,
    val fecha: String,
    val hora: String,
    val titulo: String,
    val referencia: String,
    val pasajeros: Int,
    val transportistaId: String? = null,
    val guiaId: String? = null,
    val motivoRechazo: String? = null,
    @DrawableRes val imagen: Int? = null
)

fun ServicioLinceDto.toServicioUi(): Servicio {
    val tipoMapeado = when (this.tipo.uppercase()) {
        "TRANSFER" -> TipoServicio.TRANSFER
        "GUIDED_SERVICE" -> TipoServicio.GUIADO
        else -> TipoServicio.EXCURSION
    }

    // Prioriza el estado de asignación del staff (StatusPL) y usa estadoServicio como respaldo
    val codigoEstado = (this.asignacion?.statusPL ?: this.estadoServicio ?: "RQ").uppercase()
    val estadoMapeado = when (codigoEstado) {
        "CF" -> EstadoServicio.CONFIRMADO
        "XX", "CX", "DL" -> EstadoServicio.RECHAZADO
        "WL" -> EstadoServicio.MODIFICADO
        else -> EstadoServicio.PENDIENTE
    }

    val pickupTexto = this.pickup?.takeIf { it.isNotBlank() } ?: "Origen por definir"
    val dropoffTexto = this.dropoff?.takeIf { it.isNotBlank() } ?: "Destino por definir"


    val tituloFinal = this.descripcion?.takeIf { it.isNotBlank() }
        ?: this.descripcionPadre?.takeIf { it.isNotBlank() }
        ?: if (tipoMapeado == TipoServicio.TRANSFER) "$pickupTexto → $dropoffTexto"
        else "Servicio ${this.codigo ?: this.id}: $pickupTexto"

    val infoSubtitulo = buildString {
        append("$pickupTexto → $dropoffTexto")
        this@toServicioUi.reserva?.referencia?.takeIf { it.isNotBlank() }?.let { ref ->
            append(" • Ref: $ref")
        }
        this@toServicioUi.notas?.takeIf { it.isNotBlank() }?.let { nota ->
            append(" • $nota")
        }
    }

    // Calcula total de pasajeros desde el objeto pax (o parsea el texto "adultos+niños")
    val totalPasajeros = this.pax?.total
        ?: ((this.pax?.adultos ?: 0) + (this.pax?.ninos ?: 0)).takeIf { it > 0 }
        ?: this.pax?.texto?.split("+")?.sumOf { it.trim().toIntOrNull() ?: 0 }
        ?: 0

    return Servicio(
        id = this.id,
        tipo = tipoMapeado,
        estado = estadoMapeado,
        fecha = this.fecha,
        hora = this.horaPickup ?: "--:--",
        titulo = tituloFinal,
        referencia = infoSubtitulo,
        pasajeros = totalPasajeros,
        transportistaId = this.transportista?.id ?: this.transportista?.nombre,
        guiaId = this.guia?.id ?: this.guia?.nombre,
        imagen = if (tipoMapeado == TipoServicio.EXCURSION) R.drawable.volcan_osorno else null
    )
}