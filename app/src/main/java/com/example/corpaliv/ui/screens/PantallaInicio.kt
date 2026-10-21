package com.example.corpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.corpaliv.navigation.Destino

private data class Modalidad(
    val titulo: String,
    val descripcion: String,
    val textoBoton: String,
    val destino: Destino
)

private val modalidades = listOf(
    Modalidad(
        titulo = "Donación",
        descripcion = "Aporta a la organización. Puedes dirigir tu aporte a la Escuela Especial " +
                "Jan Van Dijk, a la Red Sociolaboral Alba Lab o donde más se necesite.",
        textoBoton = "Quiero donar",
        destino = Destino.DONACIONES
    ),
    Modalidad(
        titulo = "Compra de productos",
        descripcion = "Conoce los productos y servicios con impacto social de la red sociolaboral.",
        textoBoton = "Ver catálogo",
        destino = Destino.CATALOGO
    ),
    Modalidad(
        titulo = "Alianzas",
        descripcion = "Empresas y organizaciones pueden construir una alianza con CORPALIV.",
        textoBoton = "Solicitar contacto",
        destino = Destino.CONTACTO
    ),
    Modalidad(
        titulo = "Voluntariado",
        descripcion = "Súmate como voluntario o voluntaria a las iniciativas de la organización.",
        textoBoton = "Quiero participar",
        destino = Destino.CONTACTO
    ),
    Modalidad(
        titulo = "Capacitaciones",
        descripcion = "Conoce las instancias de formación y preparación para el empleo.",
        textoBoton = "Quiero saber más",
        destino = Destino.CONTACTO
    )
)

@Composable
fun PantallaInicio(
    onIrA: (Destino) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Aparece(visible = visible, retardo = 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Alba Conecta",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Conoce el trabajo de CORPALIV y Alba Lab y descubre cómo puedes sumarte.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Aparece(visible = visible, retardo = 150) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quiénes somos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CORPALIV es una organización sin fines de lucro que promueve la " +
                                "autonomía, la participación y la inclusión social y laboral de " +
                                "personas con discapacidad múltiple.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "A través de la Escuela Especial Jan Van Dijk y de su Red " +
                                "Sociolaboral Alba Lab, desarrolla programas de formación, " +
                                "preparación para el empleo, producción y comercialización de productos.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Aparece(visible = visible, retardo = 300) {
            Text(
                text = "Cómo puedes colaborar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        modalidades.forEachIndexed { indice, modalidad ->
            Aparece(visible = visible, retardo = 400 + indice * 100) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = modalidad.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = modalidad.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        TextButton(onClick = { onIrA(modalidad.destino) }) {
                            Text(modalidad.textoBoton)
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun Aparece(
    visible: Boolean,
    retardo: Int,
    contenido: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 500, delayMillis = retardo)) +
                slideInVertically(tween(durationMillis = 500, delayMillis = retardo)) { it / 4 }
    ) {
        contenido()
    }
}