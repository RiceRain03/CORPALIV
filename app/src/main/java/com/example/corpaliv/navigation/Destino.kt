package com.example.corpaliv.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destino(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector,
    val enBarraInferior: Boolean
) {
    INICIO("inicio", "Inicio", Icons.Filled.Home, true),
    PROGRAMAS("programas", "Programas", Icons.Filled.Info, false),
    CATALOGO("catalogo", "Catálogo", Icons.Filled.ShoppingCart, true),
    CONTACTO("contacto", "Contacto", Icons.Filled.Email, true),
    CUENTA("cuenta", "Mi cuenta", Icons.Filled.Person, true),
    DONACIONES("donaciones", "Donaciones", Icons.Filled.Favorite, true);

    companion object {
        fun desdeRuta(ruta: String?): Destino =
            entries.firstOrNull { it.ruta == ruta } ?: INICIO
    }
}