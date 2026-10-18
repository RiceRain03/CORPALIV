package com.example.corpaliv.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.corpaliv.ui.components.PantallaBase

@Composable
fun PantallaContacto(modifier: Modifier = Modifier) {
    PantallaBase(
        titulo = "Contacto",
        descripcion = "Aqui ira la informacion base para poder contactar a la empresa",
        modifier = modifier
    )
}