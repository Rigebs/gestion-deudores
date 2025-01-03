package com.rige.gestiondeudores.models

data class Venta(
    val id: Int? = null,
    val monto: Double,
    val fechaVenta: String?,
    val estado: Boolean,
    val descripcion: String?,
    val fechaPago: String? = null,
    val clienteId: Int
)