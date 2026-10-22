package com.example.corpaliv.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
                AnimatedVisibility(
                    visible = viewModel.errorNombre != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    viewModel.errorNombre?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
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

        // 3. Campo Fecha (Date)
        OutlinedTextField(
            value = viewModel.fecha,
            onValueChange = { viewModel.fecha = it },
            label = { Text("Fecha de Contacto (DD/MM/AAAA)") },
            isError = viewModel.errorFecha != null,
            trailingIcon = {
                if (viewModel.errorFecha != null) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            supportingText = {
                viewModel.errorFecha?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Switch (¿Es Empresa?)
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

        // 5. Mensaje adicional (String)
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

        // Botón para enviar/validar y guardar en Room
        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Registro")
        }
        AnimatedVisibility(
            visible = viewModel.esGuardadoExitoso,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "¡Registro guardado exitosamente en la base de datos local (Room)!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = "Registros en Base de Datos Local (${contactos.size})",
            style = MaterialTheme.typography.titleMedium
        )

        // Diálogo de retroalimentación al guardar con éxito
        if (viewModel.esGuardadoExitoso) {
            AlertDialog(
                onDismissRequest = { viewModel.esGuardadoExitoso = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("¡Guardado exitoso!") },
                text = { Text("Los datos se han guardado correctamente en la base de datos local (Room).") },
                confirmButton = {
                    TextButton(onClick = { viewModel.esGuardadoExitoso = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}