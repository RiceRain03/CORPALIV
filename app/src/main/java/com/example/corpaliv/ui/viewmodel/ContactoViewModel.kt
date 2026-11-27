package com.example.corpaliv.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpaliv.data.local.AppDatabase
import com.example.corpaliv.data.local.ContactoEntity
import com.example.corpaliv.data.local.SesionDataStore
import com.example.corpaliv.data.remote.ContactoDto
import com.example.corpaliv.data.remote.TokenStore
import com.example.corpaliv.data.repository.ContactoRemotoRepository
import com.example.corpaliv.data.repository.mensajeAmigable
import kotlinx.coroutines.launch

class ContactoViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val TIPOS = listOf("DONACION", "VOLUNTARIADO", "ALIANZA_RSE", "COMPRA", "CAPACITACION", "OTRO")
        val ESTADOS = listOf("RECIBIDA", "EN_REVISION", "CONTACTADO", "CONCRETADO", "DESCARTADO")
    }

    private val dao = AppDatabase.getDatabase(application).contactoDao()
    private val sesion = SesionDataStore(application)
    private val remoto = ContactoRemotoRepository()

    // Formulario
    var nombre by mutableStateOf("")
    var edad by mutableStateOf("")
    var fecha by mutableStateOf("")
    var esEmpresa by mutableStateOf(false)
    var aceptaTerminos by mutableStateOf(false)
    var mensaje by mutableStateOf("")
    var tipoColaboracion by mutableStateOf("OTRO")

    var errorNombre by mutableStateOf<String?>(null)
    var errorEdad by mutableStateOf<String?>(null)
    var errorFecha by mutableStateOf<String?>(null)
    var errorTerminos by mutableStateOf<String?>(null)
    var errorServidor by mutableStateOf<String?>(null)
    var esGuardadoExitoso by mutableStateOf(false)
    var enviando by mutableStateOf(false)
        private set

    // Panel del gestor (solo rol ADMIN)
    var esAdminAutenticado by mutableStateOf(false)
        private set
    var cargandoLista by mutableStateOf(false)
        private set
    var errorLista by mutableStateOf<String?>(null)
    val contactosRemotos = mutableStateListOf<ContactoDto>()

    fun validarYGuardar() {
        var esValido = true

        if (nombre.isBlank()) {
            errorNombre = "El nombre es obligatorio"
            esValido = false
        } else {
            errorNombre = null
        }

        val edadInt = edad.toIntOrNull()
        if ((edadInt == null) || (edadInt <= 0)) {
            errorEdad = "Ingrese una edad válida"
            esValido = false
        } else {
            errorEdad = null
        }

        if (fecha.isBlank()) {
            errorFecha = "La fecha es requerida"
            esValido = false
        } else {
            errorFecha = null
        }

        if (!aceptaTerminos) {
            errorTerminos = "Debe aceptar los términos"
            esValido = false
        } else {
            errorTerminos = null
        }

        if (!esValido) return

        viewModelScope.launch {
            enviando = true
            errorServidor = null

            val dto = ContactoDto(
                nombre = nombre.trim(),
                edad = edadInt!!,
                fechaContacto = fecha,
                esEmpresa = esEmpresa,
                aceptaTerminos = aceptaTerminos,
                mensaje = mensaje,
                tipoColaboracion = tipoColaboracion
            )

            remoto.crear(dto)
                .onSuccess {

                    dao.insertarContacto(
                        ContactoEntity(
                            nombre = dto.nombre,
                            edad = dto.edad,
                            fechaContacto = dto.fechaContacto,
                            esEmpresa = dto.esEmpresa,
                            aceptaTerminos = dto.aceptaTerminos,
                            mensaje = dto.mensaje ?: ""
                        )
                    )
                    esGuardadoExitoso = true
                    limpiarCampos()
                    if (esAdminAutenticado) cargarContactos()
                }
                .onFailure { errorServidor = it.mensajeAmigable() }

            enviando = false
        }
    }

    private fun limpiarCampos() {
        nombre = ""
        edad = ""
        fecha = ""
        esEmpresa = false
        aceptaTerminos = false
        mensaje = ""
        tipoColaboracion = "OTRO"
    }


    fun refrescarAdmin() {
        viewModelScope.launch {
            sesion.restaurarToken()
            esAdminAutenticado = TokenStore.rol == "ADMIN"
            if (esAdminAutenticado) cargarContactos()
        }
    }

    fun cargarContactos() {
        viewModelScope.launch {
            cargandoLista = true
            errorLista = null
            remoto.listar()
                .onSuccess {
                    contactosRemotos.clear()
                    contactosRemotos.addAll(it)
                }
                .onFailure { errorLista = it.mensajeAmigable() }
            cargandoLista = false
        }
    }

    fun cambiarEstado(id: Long, estado: String) {
        viewModelScope.launch {
            remoto.cambiarEstado(id, estado)
                .onSuccess { cargarContactos() }
                .onFailure { errorLista = it.mensajeAmigable() }
        }
    }

    fun eliminar(id: Long) {
        viewModelScope.launch {
            remoto.eliminar(id)
                .onSuccess { cargarContactos() }
                .onFailure { errorLista = it.mensajeAmigable() }
        }
    }
}