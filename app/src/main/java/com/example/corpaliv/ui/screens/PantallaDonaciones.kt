package com.example.corpaliv.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.corpaliv.Notificador
import com.example.corpaliv.ui.viewmodel.DonacionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun formatearFechaDonacion(milisegundos: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formato.timeZone = TimeZone.getTimeZone("UTC")
    return formato.format(Date(milisegundos))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDonaciones(
    modifier: Modifier = Modifier,
    viewModel: DonacionViewModel = viewModel()
) {
    val contexto = LocalContext.current
    var mostrarCalendario by remember { mutableStateOf(false) }
    var destinoExpandido by remember { mutableStateOf(false) }


    val permisoNotificacionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        viewModel.guardar()
    }

    fun registrarDonacion() {
        if (!viewModel.formularioValido()) return

        if (Notificador.tienePermiso(contexto)) {
            viewModel.guardar()
        } else {
            permisoNotificacionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.autorrellenar()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Quiero donar",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Este formulario registra tu intención de donar. " ,
            style = MaterialTheme.typography.bodySmall
        )

        AnimatedVisibility(visible = viewModel.datosDeCuenta) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Completamos tu nombre con los datos de tu cuenta",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }


        AnimatedVisibility(
            visible = !viewModel.esAnonima,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                isError = viewModel.errorNombre != null,
                trailingIcon = { IconoCampoError(viewModel.errorNombre != null) },
                supportingText = { viewModel.errorNombre?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }


        OutlinedTextField(
            value = viewModel.monto,
            onValueChange = { viewModel.actualizarMonto(it) },
            label = { Text("Monto") },
            prefix = { Text("\$") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = viewModel.errorMonto != null,
            trailingIcon = { IconoCampoError(viewModel.errorMonto != null) },
            supportingText = { Text(viewModel.errorMonto ?: "Entre \$1.000 y \$1.000.000") },
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            OutlinedTextField(
                value = viewModel.fecha?.let { formatearFechaDonacion(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de tu aporte") },
                placeholder = { Text("Toca para elegir una fecha") },
                isError = viewModel.errorFecha != null,
                trailingIcon = {
                    if (viewModel.errorFecha != null) {
                        IconoCampoError(true)
                    } else {
                        Icon(Icons.Filled.DateRange, contentDescription = "Abrir calendario")
                    }
                },
                supportingText = { viewModel.errorFecha?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { mostrarCalendario = true }
            )
        }


        ExposedDropdownMenuBox(
            expanded = destinoExpandido,
            onExpandedChange = { destinoExpandido = it }
        ) {
            OutlinedTextField(
                value = viewModel.destino,
                onValueChange = {},
                readOnly = true,
                label = { Text("Destino del aporte") },
                isError = viewModel.errorDestino != null,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = destinoExpandido)
                },
                supportingText = { viewModel.errorDestino?.let { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = destinoExpandido,
                onDismissRequest = { destinoExpandido = false }
            ) {
                viewModel.destinos.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            viewModel.destino = opcion
                            destinoExpandido = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Quiero donar de forma anónima")
            Switch(
                checked = viewModel.esAnonima,
                onCheckedChange = { viewModel.esAnonima = it }
            )
        }


        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = viewModel.aceptaTerminos,
                onCheckedChange = { viewModel.aceptaTerminos = it }
            )
            Text("Acepto los términos y condiciones")
        }
        viewModel.errorTerminos?.let { mensaje ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoCampoError(true)
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Button(
            onClick = { registrarDonacion() },
            enabled = !viewModel.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Registrar donación")
            }
        }
    }


    if (mostrarCalendario) {
        val estadoCalendario = rememberDatePickerState(
            initialSelectedDateMillis = viewModel.fecha,

            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= viewModel.fechaMinimaUtc()
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.fecha = estadoCalendario.selectedDateMillis
                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estadoCalendario)
        }
    }

    if (viewModel.mostrarExito) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarExito() },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("¡Gracias por tu aporte!") },
            text = {
                Text("Tu intención de donar quedó registrada. ")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.cerrarExito() }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun IconoCampoError(visible: Boolean) {
    if (visible) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
    }
}