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
    val contactos by viewModel.contactos.collectAsState()
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = viewModel.aceptaTerminos,
                onCheckedChange = { viewModel.aceptaTerminos = it }
            )
            Text(
                text = "Acepto los términos de tratamiento de datos ficticios",
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

        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Registro")
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
                        text = "Los registros de contacto son confidenciales y solo accesibles por administradores.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.mostrarDialogoAdmin = true }) {
                        Text("Ingresar como Administrador")
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
                    text = "Registros en Base de Datos (${contactos.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = { viewModel.cerrarSesionAdmin() }) {
                    Text("Cerrar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (contactos.isEmpty()) {
                Text(
                    text = "No hay registros guardados aún.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                contactos.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Nombre: ${item.nombre}", style = MaterialTheme.typography.titleSmall)
                            Text(text = "Edad: ${item.edad} | Fecha: ${item.fechaContacto}")
                            Text(text = "Empresa: ${if (item.esEmpresa) "Sí" else "No"}")
                            if (item.mensaje.isNotBlank()) {
                                Text(text = "Mensaje: ${item.mensaje}")
                            }
                        }
                    }
                }
            }
        }

        if (viewModel.mostrarDialogoAdmin) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.mostrarDialogoAdmin = false
                    viewModel.errorClaveAdmin = null
                    viewModel.claveAdmin = ""
                },
                title = { Text("Acceso Administrador") },
                text = {
                    Column {
                        Text("Ingrese la clave para desbloquear los datos guardados:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = viewModel.claveAdmin,
                            onValueChange = { viewModel.claveAdmin = it },
                            label = { Text("Contraseña (admin123)") },
                            singleLine = true,
                            isError = viewModel.errorClaveAdmin != null,
                            supportingText = {
                                viewModel.errorClaveAdmin?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.validarClaveAdmin() }) {
                        Text("Ingresar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.mostrarDialogoAdmin = false
                        viewModel.errorClaveAdmin = null
                        viewModel.claveAdmin = ""
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}