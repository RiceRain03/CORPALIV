package com.example.corpaliv.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.corpaliv.data.local.FotoPerfil
import com.example.corpaliv.ui.components.FotoPerfilCircular
import com.example.corpaliv.ui.viewmodel.CuentaViewModel

@Composable
fun PantallaCuenta(
    modifier: Modifier = Modifier,
    viewModel: CuentaViewModel = viewModel()
) {
    val usuario = viewModel.usuarioActual
    val contexto = LocalContext.current

    var archivoPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var aviso by remember { mutableStateOf<String?>(null) }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        val ruta = archivoPendiente
        if (exito && ruta != null) {
            viewModel.actualizarFoto(ruta)
            aviso = null
        } else {
            FotoPerfil.borrar(ruta)
        }
        archivoPendiente = null
    }

    fun abrirCamara() {
        val id = viewModel.usuarioActual?.id ?: return
        val archivo = FotoPerfil.crearArchivoFoto(contexto, id)
        archivoPendiente = archivo.absolutePath
        camaraLauncher.launch(FotoPerfil.uriSegura(contexto, archivo))
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            aviso = null
            abrirCamara()
        } else {
            aviso = "Sin el permiso de cámara no se puede tomar la foto. " +
                    "Puedes activarlo en Ajustes de la aplicación."
        }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.guardarFotoDesdeGaleria(uri)
            aviso = null
        }
    }

    fun tomarFoto() {
        val concedido = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (concedido) abrirCamara() else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (viewModel.restaurando) {
            CircularProgressIndicator()
        } else if (usuario != null) {
            SesionIniciada(
                nombre = usuario.nombre,
                correo = usuario.correo,
                fotoRuta = usuario.fotoRuta,
                aviso = aviso,
                onTomarFoto = { tomarFoto() },
                onElegirGaleria = { galeriaLauncher.launch("image/*") },
                onCerrarSesion = { viewModel.cerrarSesion() }
            )
        } else {
            FormularioCuenta(viewModel)
        }
    }
}

@Composable
private fun FormularioCuenta(viewModel: CuentaViewModel) {
    Text(
        text = if (viewModel.esRegistro) "Crear cuenta" else "Iniciar sesión",
        style = MaterialTheme.typography.headlineSmall
    )
    Text(
        text = "La cuenta es opcional: sirve para autorrellenar tus datos al donar o contactar. "
                ,
        style = MaterialTheme.typography.bodySmall
    )

    AnimatedVisibility(
        visible = viewModel.esRegistro,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            isError = viewModel.errorNombre != null,
            trailingIcon = { IconoError(viewModel.errorNombre != null) },
            supportingText = { viewModel.errorNombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
    }

    OutlinedTextField(
        value = viewModel.correo,
        onValueChange = { viewModel.correo = it },
        label = { Text("Correo") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = viewModel.errorCorreo != null,
        trailingIcon = { IconoError(viewModel.errorCorreo != null) },
        supportingText = { viewModel.errorCorreo?.let { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = viewModel.clave,
        onValueChange = { viewModel.clave = it },
        label = { Text("Clave") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = viewModel.errorClave != null,
        trailingIcon = { IconoError(viewModel.errorClave != null) },
        supportingText = { viewModel.errorClave?.let { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )

    AnimatedVisibility(
        visible = viewModel.esRegistro,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        OutlinedTextField(
            value = viewModel.confirmarClave,
            onValueChange = { viewModel.confirmarClave = it },
            label = { Text("Confirmar clave") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = viewModel.errorConfirmacion != null,
            trailingIcon = { IconoError(viewModel.errorConfirmacion != null) },
            supportingText = { viewModel.errorConfirmacion?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
    }

    AnimatedVisibility(visible = viewModel.errorGeneral != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = viewModel.errorGeneral ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    Button(
        onClick = {
            if (viewModel.esRegistro) viewModel.registrar() else viewModel.iniciarSesion()
        },
        enabled = !viewModel.cargando,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (viewModel.cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(if (viewModel.esRegistro) "Registrarme" else "Entrar")
        }
    }

    TextButton(
        onClick = { viewModel.cambiarModo(!viewModel.esRegistro) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            if (viewModel.esRegistro) "¿Ya tienes cuenta? Inicia sesión"
            else "¿No tienes cuenta? Regístrate"
        )
    }
}

@Composable
private fun SesionIniciada(
    nombre: String,
    correo: String,
    fotoRuta: String?,
    aviso: String?,
    onTomarFoto: () -> Unit,
    onElegirGaleria: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FotoPerfilCircular(fotoRuta = fotoRuta, tamano = 120.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sesión iniciada",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(text = "Hola, $nombre", style = MaterialTheme.typography.bodyLarge)
            Text(text = correo, style = MaterialTheme.typography.bodyMedium)

            Button(
                onClick = onTomarFoto,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📷  Tomar foto")
            }
            OutlinedButton(
                onClick = onElegirGaleria,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🖼️  Elegir de la galería")
            }

            AnimatedVisibility(visible = aviso != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = aviso ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }

    OutlinedButton(
        onClick = onCerrarSesion,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cerrar sesión")
    }
}

@Composable
private fun IconoError(visible: Boolean) {
    if (visible) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
    }
}