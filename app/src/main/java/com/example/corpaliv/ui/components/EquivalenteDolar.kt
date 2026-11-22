package com.example.corpaliv.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.corpaliv.ui.viewmodel.IndicadoresViewModel
import java.util.Locale

@Composable
fun EquivalenteDolar(
    monto: String,
    modifier: Modifier = Modifier,
    viewModel: IndicadoresViewModel = viewModel()
) {
    val chile = Locale.forLanguageTag("es-CL")
    val montoNumero = monto.toIntOrNull()
    val dolar = viewModel.dolar

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            viewModel.cargando -> {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Text(
                    text = "Consultando el valor del dólar…",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            viewModel.error != null -> {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = viewModel.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 8.dp)
                )
                TextButton(onClick = { viewModel.cargar() }) { Text("Reintentar") }
            }

            dolar != null -> {
                val dolarTexto = String.format(chile, "%,.2f", dolar.valor)
                val fecha = dolar.fecha.take(10)
                val usd = if (montoNumero != null && montoNumero > 0) viewModel.clpAUsd(montoNumero) else null

                Text(
                    text = if (usd != null) {
                        "≈ US$ ${String.format(chile, "%,.2f", usd)}  (dólar a \$$dolarTexto, $fecha)"
                    } else {
                        "Dólar observado: \$$dolarTexto ($fecha)"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}