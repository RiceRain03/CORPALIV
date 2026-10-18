package com.example.corpaliv.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.corpaliv.ui.components.PantallaBase

@Composable
fun PantallaPrograma(modifier: Modifier = Modifier) {
    PantallaBase(
        titulo = "Programa ",
        descripcion = "Aqui ira la infomracion base para poder informarce sobre el programa",
        modifier = modifier
    )
}