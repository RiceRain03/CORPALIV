package com.example.corpaliv.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.corpaliv.ui.components.PantallaBase

@Composable
fun InicioScreen(modifier: Modifier = Modifier) {
    PantallaBase(
        titulo = "Inicio",
        descripcion = "Aquí irá la presentación de CORPALIV y Alba Lab.",
        modifier = modifier
    )
}