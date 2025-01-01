package com.rige.gestiondeudores.models

data class Cliente(
    val id: Int? = null,
    val nombre: String,
    val telefono: String?,
    val direccion: String?
)