package com.example.corpaliv.data.model

data class DolarRespuesta(
    val codigo: String,
    val nombre: String,
    val serie: List<ValorIndicador>
)

data class ValorIndicador(
    val fecha: String,
    val valor: Double
)

data class ValorDolar(
    val valor: Double,
    val fecha: String
)