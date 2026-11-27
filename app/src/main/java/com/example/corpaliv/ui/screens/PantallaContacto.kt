package com.example.corpaliv.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.corpaliv.ui.viewmodel.ContactoViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.horizontalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContacto(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val viewModel: ContactoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ContactoViewModel(context.applicationContext as Application) as T
            }
        }
    )
    val contactos = viewModel.contactosRemotos
    LaunchedEffect(Unit) { viewModel.refrescarAdmin() }
    LaunchedEffect(viewModel.esGuardadoExitoso) {
        if (viewModel.esGuardadoExitoso) {
            delay(4.seconds)
            viewModel.esGuardadoExitoso = false
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Formulario de Contacto y Vinculación",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            label = { Text("Nombre Completo") },
            isError = viewModel.errorNombre != null,
            trailingIcon = {
                if (viewModel.errorNombre != null) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            supportingText = {
                Column {
                    AnimatedVisibility(
                        visible = viewModel.errorNombre != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        viewModel.errorNombre?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.edad,
            onValueChange = { viewModel.edad = it },
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = viewModel.errorEdad != null,
            trailingIcon = {
                if (viewModel.errorEdad != null) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            supportingText = {
                viewModel.errorEdad?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        var mostrarDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = viewModel.fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de Contacto") },
                placeholder = { Text("Selecciona una fecha") },
                trailingIcon = {
                    IconButton(onClick = { mostrarDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha"
                        )
                    }
                },
                isError = viewModel.errorFecha != null,
                supportingText = {
                    Column {
                        AnimatedVisibility(
                            visible = viewModel.errorFecha != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            viewModel.errorFecha?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { mostrarDatePicker = true }
            )
        }

        if (mostrarDatePicker) {
            DatePickerDialog(
                onDismissRequest = { mostrarDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                formatter.timeZone = TimeZone.getTimeZone("UTC")
                                viewModel.fecha = formatter.format(Date(millis))
                            }
                            mostrarDatePicker = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Representa a una Empresa u Organización?")
            Switch(
                checked = viewModel.esEmpresa,
                onCheckedChange = { viewModel.esEmpresa = it }
            )
        }

        OutlinedTextField(
            value = viewModel.mensaje,
            onValueChange = { viewModel.mensaje = it },
            label = { Text("Mensaje o Propuesta") },
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Tipo de colaboración", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContactoViewModel.TIPOS.forEach { tipo ->
                FilterChip(
                    selected = viewModel.tipoColaboracion == tipo,
                    onClick = { viewModel.tipoColaboracion = tipo },
                    label = { Text(tipo.replace("_", " ")) }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = viewModel.aceptaTerminos,
                onCheckedChange = { viewModel.aceptaTerminos = it }
            )
            Text(
                text = "Acepto los términos de tratamiento de datos",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        viewModel.errorTerminos?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        viewModel.errorServidor?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = { viewModel.validarYGuardar() },
            enabled = !viewModel.enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.enviando) "Enviando..." else "Enviar Registro")
        }
        if (!viewModel.esAdminAutenticado) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Las solicitudes son confidenciales y solo las ve el equipo de vinculación (rol ADMIN).",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inicia sesión con una cuenta de gestor en «Mi cuenta» y vuelve aquí.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refrescarAdmin() }) {
                        Text("Ya inicié sesión, verificar")
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Solicitudes recibidas (${contactos.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { viewModel.cargarContactos() }) {
                    Text("Actualizar")
                }
            }

            if (viewModel.cargandoLista) {
                CircularProgressIndicator()
            }
            viewModel.errorLista?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (contactos.isEmpty() && !viewModel.cargandoLista) {
                Text(
                    text = "No hay solicitudes aún.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            contactos.forEach { item ->
                key(item.id) {
                    var menuEstado by remember { mutableStateOf(false) }
                    var confirmarEliminar by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${item.nombre} · ${(item.tipoColaboracion ?: "OTRO").replace("_", " ")}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(text = "Edad: ${item.edad} | Fecha: ${item.fechaContacto}")
                            Text(text = "Empresa: ${if (item.esEmpresa) "Sí" else "No"}")
                            if (!item.mensaje.isNullOrBlank()) {
                                Text(text = "Mensaje: ${item.mensaje}")
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box {
                                    OutlinedButton(onClick = { menuEstado = true }) {
                                        Text("Estado: ${item.estado ?: "RECIBIDA"}")
                                    }
                                    DropdownMenu(
                                        expanded = menuEstado,
                                        onDismissRequest = { menuEstado = false }
                                    ) {
                                        ContactoViewModel.ESTADOS.forEach { estado ->
                                            DropdownMenuItem(
                                                text = { Text(estado) },
                                                onClick = {
                                                    menuEstado = false
                                                    item.id?.let { viewModel.cambiarEstado(it, estado) }
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = { confirmarEliminar = true }) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }

                    if (confirmarEliminar) {
                        AlertDialog(
                            onDismissRequest = { confirmarEliminar = false },
                            title = { Text("Eliminar solicitud") },
                            text = { Text("¿Eliminar la solicitud de ${item.nombre}? Esta acción no se puede deshacer.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    confirmarEliminar = false
                                    item.id?.let { viewModel.eliminar(it) }
                                }) { Text("Eliminar") }
                            },
                            dismissButton = {
                                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
                            }
                        )
                    }
                }
            }
        }
    }
}