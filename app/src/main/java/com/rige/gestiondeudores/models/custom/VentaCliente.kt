package com.rige.gestiondeudores.models.custom

data class VentaCliente(
    val id: Int,
    val monto: Double,
    val fechaVenta: String,
    val estado: Boolean,
    val descripcion: String?,
    val fechaPago: String?,
    val clienteId: Int,
    val nombreCliente: String
)
