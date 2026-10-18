package com.example.corpaliv.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.corpaliv.ui.components.PantallaBase

@Composable
fun PantallaDonaciones(modifier: Modifier = Modifier) {
    PantallaBase(
        titulo = "Donaciones",
        descripcion = "Aqui ira la infomracion para poder realizar una o mas donaciones",
        modifier = modifier
    )
}