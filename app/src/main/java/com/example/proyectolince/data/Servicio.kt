package com.example.proyectolince.data

// Se define los dos tipos de operaciones que maneja Southbound
enum class TipoServicio {
    TRANSFER,
    EXCURSION
}

// Estados del traslado o tour
enum class EstadoServicio {
    PENDIENTE,
    CONFIRMADO
}

//Estructura de datos que representa cada tarjeta en la lista
data class Servicio(
    val id: String,
    val tipo: TipoServicio,
    val estado: EstadoServicio,
    val fechaHora : String,
    val titulo : String,
    val detalleSubtitulo : String,
    val detalleInformativo : String? = null,
    val pasajeros: String,
    val vehiculoOInfo : String
)