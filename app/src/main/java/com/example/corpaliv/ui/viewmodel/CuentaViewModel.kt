package com.example.corpaliv.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpaliv.data.local.AppDatabase
import com.example.corpaliv.data.local.UsuarioEntity
import com.example.corpaliv.data.local.seguridad.ClaveSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CuentaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).usuarioDao()

    var esRegistro by mutableStateOf(false)
        private set

    // Campos del formulario
    var nombre by mutableStateOf("")
    var correo by mutableStateOf("")
    var clave by mutableStateOf("")
    var confirmarClave by mutableStateOf("")

    var errorNombre by mutableStateOf<String?>(null)
    var errorCorreo by mutableStateOf<String?>(null)
    var errorClave by mutableStateOf<String?>(null)
    var errorConfirmacion by mutableStateOf<String?>(null)
    var errorGeneral by mutableStateOf<String?>(null)

    var cargando by mutableStateOf(false)
        private set

    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)
        private set

    fun cambiarModo(registro: Boolean) {
        esRegistro = registro
        limpiarErrores()
    }

    fun registrar() {
        if (!validarRegistro()) return

        viewModelScope.launch {
            cargando = true
            val correoLimpio = correo.trim().lowercase()

            if (dao.buscarPorCorreo(correoLimpio) != null) {
                errorCorreo = "Este correo ya está registrado"
                cargando = false
                return@launch
            }

            val sal = ClaveSegura.generarSal()
            val hash = withContext(Dispatchers.Default) { ClaveSegura.cifrar(clave, sal) }

            dao.insertar(
                UsuarioEntity(
                    nombre = nombre.trim(),
                    correo = correoLimpio,
                    claveHash = hash,
                    sal = sal
                )
            )

            usuarioActual = dao.buscarPorCorreo(correoLimpio)
            limpiarCampos()
            cargando = false
        }
    }

    fun iniciarSesion() {
        if (!validarInicioSesion()) return

        viewModelScope.launch {
            cargando = true
            val usuario = dao.buscarPorCorreo(correo.trim().lowercase())

            val claveCorrecta = usuario != null && withContext(Dispatchers.Default) {
                ClaveSegura.verificar(clave, usuario.sal, usuario.claveHash)
            }

            if (claveCorrecta) {
                usuarioActual = usuario
                limpiarCampos()
            } else {
                errorGeneral = "Correo o clave incorrectos"
            }
            cargando = false
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        limpiarCampos()
    }


    private fun validarRegistro(): Boolean {
        limpiarErrores()
        var valido = true

        if (nombre.isBlank()) {
            errorNombre = "El nombre es obligatorio"
            valido = false
        } else if (nombre.trim().length < 3) {
            errorNombre = "El nombre debe tener al menos 3 caracteres"
            valido = false
        }

        if (!correoValido()) valido = false

        if (clave.length < 6) {
            errorClave = "La clave debe tener al menos 6 caracteres"
            valido = false
        }

        if (confirmarClave != clave) {
            errorConfirmacion = "Las claves no coinciden"
            valido = false
        }

        return valido
    }

    private fun validarInicioSesion(): Boolean {
        limpiarErrores()
        var valido = true

        if (!correoValido()) valido = false

        if (clave.isBlank()) {
            errorClave = "Ingresa tu clave"
            valido = false
        }

        return valido
    }

    private fun correoValido(): Boolean {
        return when {
            correo.isBlank() -> {
                errorCorreo = "El correo es obligatorio"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> {
                errorCorreo = "Ingresa un correo válido"
                false
            }

            else -> true
        }
    }

    private fun limpiarErrores() {
        errorNombre = null
        errorCorreo = null
        errorClave = null
        errorConfirmacion = null
        errorGeneral = null
    }

    private fun limpiarCampos() {
        nombre = ""
        correo = ""
        clave = ""
        confirmarClave = ""
        limpiarErrores()
    }
}