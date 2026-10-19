package com.example.corpaliv.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpaliv.data.local.AppDatabase
import com.example.corpaliv.data.local.ContactoEntity
import kotlinx.coroutines.launch

class ContactoViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).contactoDao()

    var nombre by mutableStateOf("")
    var edad by mutableStateOf("")
    var fecha by mutableStateOf("")
    var esEmpresa by mutableStateOf(false)
    var aceptaTerminos by mutableStateOf(false)
    var mensaje by mutableStateOf("")

    var errorNombre by mutableStateOf<String?>(null)
    var errorEdad by mutableStateOf<String?>(null)
    var errorFecha by mutableStateOf<String?>(null)
    var errorTerminos by mutableStateOf<String?>(null)
    var esGuardadoExitoso by mutableStateOf(false)

    fun validarYGuardar() {
        var esValido = true

        if (nombre.isBlank()) {
            errorNombre = "El nombre es obligatorio"
            esValido = false
        } else {
            errorNombre = null
        }

        val edadInt = edad.toIntOrNull()
        if (edadInt == null || edadInt <= 0) {
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

        if (esValido) {
            viewModelScope.launch {
                val nuevoContacto = ContactoEntity(
                    nombre = nombre,
                    edad = edadInt!!,
                    fechaContacto = fecha,
                    esEmpresa = esEmpresa,
                    aceptaTerminos = aceptaTerminos,
                    mensaje = mensaje
                )
                dao.insertarContacto(nuevoContacto)
                esGuardadoExitoso = true
                limpiarCampos()
            }
        }
    }

    private fun limpiarCampos() {
        nombre = ""
        edad = ""
        fecha = ""
        esEmpresa = false
        aceptaTerminos = false
        mensaje = ""
    }
}