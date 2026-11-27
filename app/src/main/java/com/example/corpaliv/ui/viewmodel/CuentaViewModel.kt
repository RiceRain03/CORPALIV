package com.example.corpaliv.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpaliv.data.local.AppDatabase
import com.example.corpaliv.data.local.FotoPerfil
import com.example.corpaliv.data.local.SesionDataStore
import com.example.corpaliv.data.local.UsuarioEntity
import com.example.corpaliv.data.local.seguridad.ClaveSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.corpaliv.data.repository.AuthRepository
import com.example.corpaliv.data.repository.UsuarioRemotoRepository
import com.example.corpaliv.data.repository.mensajeAmigable

class CuentaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).usuarioDao()
    private val sesion = SesionDataStore(application)
    private val auth = AuthRepository()
    private val usuariosRemoto = UsuarioRemotoRepository()
    private var idRemoto: Long? = null

    var errorPerfil by mutableStateOf<String?>(null)

    var esRegistro by mutableStateOf(false)
        private set

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

    var restaurando by mutableStateOf(true)
        private set

    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)
        private set

    init {
        restaurarSesion()
    }

    private fun restaurarSesion() {
        viewModelScope.launch {
            sesion.restaurarToken()
            idRemoto = sesion.idRemoto.first()
            val correoGuardado = sesion.correoActual.first()
            if (correoGuardado != null) {
                usuarioActual = dao.buscarPorCorreo(correoGuardado)
                if (usuarioActual == null) sesion.cerrarSesion()
            }
            restaurando = false
        }
    }

    fun cambiarModo(registro: Boolean) {
        esRegistro = registro
        limpiarErrores()
    }

    fun registrar() {
        if (!validarRegistro()) return

        viewModelScope.launch {
            cargando = true
            val correoLimpio = correo.trim().lowercase()

            auth.registrar(nombre.trim(), correoLimpio, clave)
                .onSuccess { r ->
                    sesion.guardarCredenciales(r.token, r.rol, r.id)
                    idRemoto = r.id

                    if (dao.buscarPorCorreo(correoLimpio) == null) {
                        val sal = ClaveSegura.generarSal()
                        val hash = withContext(Dispatchers.Default) { ClaveSegura.cifrar(clave, sal) }
                        dao.insertar(
                            UsuarioEntity(
                                nombre = r.nombre,
                                correo = r.correo,
                                claveHash = hash,
                                sal = sal
                            )
                        )
                    }
                    usuarioActual = dao.buscarPorCorreo(correoLimpio)
                    sesion.guardarSesion(correoLimpio)
                    limpiarCampos()
                }
                .onFailure { e ->
                    val mensaje = e.mensajeAmigable()
                    if (mensaje.contains("correo", ignoreCase = true)) {
                        errorCorreo = mensaje
                    } else {
                        errorGeneral = mensaje
                    }
                }
            cargando = false
        }
    }

    fun iniciarSesion() {
        if (!validarInicioSesion()) return

        viewModelScope.launch {
            cargando = true
            val correoLimpio = correo.trim().lowercase()

            auth.login(correoLimpio, clave)
                .onSuccess { r ->
                    sesion.guardarCredenciales(r.token, r.rol, r.id)
                    idRemoto = r.id

                    var local = dao.buscarPorCorreo(correoLimpio)
                    if (local == null) {
                        val sal = ClaveSegura.generarSal()
                        val hash = withContext(Dispatchers.Default) { ClaveSegura.cifrar(clave, sal) }
                        dao.insertar(
                            UsuarioEntity(
                                nombre = r.nombre,
                                correo = r.correo,
                                claveHash = hash,
                                sal = sal
                            )
                        )
                        local = dao.buscarPorCorreo(correoLimpio)
                    }
                    usuarioActual = local
                    sesion.guardarSesion(correoLimpio)
                    limpiarCampos()
                }
                .onFailure { errorGeneral = it.mensajeAmigable() }
            cargando = false
        }
    }


    fun cerrarSesion() {
        auth.cerrarSesion()
        idRemoto = null
        usuarioActual = null
        errorPerfil = null
        limpiarCampos()
        viewModelScope.launch { sesion.cerrarSesion() }
    }

    fun guardarNombre(nuevoNombre: String) {
        val usuario = usuarioActual ?: return
        val id = idRemoto
        if (id == null) {
            errorPerfil = "Vuelve a iniciar sesión para editar tu perfil."
            return
        }
        if (nuevoNombre.trim().length < 3) {
            errorPerfil = "El nombre debe tener al menos 3 caracteres"
            return
        }
        viewModelScope.launch {
            usuariosRemoto.actualizarNombre(id, nuevoNombre.trim(), usuario.correo)
                .onSuccess {
                    dao.actualizarNombre(usuario.id, nuevoNombre.trim())
                    usuarioActual = usuario.copy(nombre = nuevoNombre.trim())
                    errorPerfil = null
                }
                .onFailure { errorPerfil = it.mensajeAmigable() }
        }
    }

    fun eliminarCuenta() {
        val usuario = usuarioActual ?: return
        val id = idRemoto
        if (id == null) {
            errorPerfil = "Vuelve a iniciar sesión para eliminar tu cuenta."
            return
        }
        viewModelScope.launch {
            usuariosRemoto.eliminar(id)
                .onSuccess {
                    FotoPerfil.borrar(usuario.fotoRuta)
                    dao.eliminar(usuario.id)
                    cerrarSesion()
                }
                .onFailure { errorPerfil = it.mensajeAmigable() }
        }
    }


    fun actualizarFoto(nuevaRuta: String) {
        val usuario = usuarioActual ?: return
        viewModelScope.launch {
            dao.actualizarFoto(usuario.id, nuevaRuta)
            FotoPerfil.borrar(usuario.fotoRuta)
            usuarioActual = usuario.copy(fotoRuta = nuevaRuta)
        }
    }

    fun guardarFotoDesdeGaleria(uri: Uri) {
        val usuario = usuarioActual ?: return
        viewModelScope.launch {
            val archivo = withContext(Dispatchers.IO) {
                FotoPerfil.copiarDesdeUri(getApplication<Application>(), uri, usuario.id)
            }
            if (archivo != null) actualizarFoto(archivo.absolutePath)
        }
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