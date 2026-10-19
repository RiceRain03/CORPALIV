package com.example.corpaliv.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.corpaliv.ui.components.PantallaBase

@Composable
fun PantallaCatalogo(modifier: Modifier = Modifier) {
    PantallaBase(
        titulo = "CATALOGO",
        descripcion = "Aqui ira el catalogo de productos con impacto social",
        modifier = modifier
    )
}