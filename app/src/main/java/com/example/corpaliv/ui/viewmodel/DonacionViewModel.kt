package com.example.corpaliv.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpaliv.Notificador
import com.example.corpaliv.data.local.AppDatabase
import com.example.corpaliv.data.local.DonacionEntity
import com.example.corpaliv.data.local.SesionDataStore
import com.example.corpaliv.data.remote.DonacionDto
import com.example.corpaliv.data.repository.DonacionRemotaRepository
import com.example.corpaliv.data.repository.mensajeAmigable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class DonacionViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val MONTO_MINIMO = 1000
        const val MONTO_MAXIMO = 1_000_000
    }

    private val baseDatos = AppDatabase.getDatabase(application)
    private val donacionDao = baseDatos.donacionDao()
    private val usuarioDao = baseDatos.usuarioDao()
    private val sesion = SesionDataStore(application)
    private val remoto = DonacionRemotaRepository()

    val destinos = listOf(
        "Escuela Especial Jan Van Dijk",
        "Red Sociolaboral Alba Lab",
        "Donde más se necesite"
    )

    // Formulario
    var nombre by mutableStateOf("")
    var monto by mutableStateOf("")
        private set
    var fecha by mutableStateOf<Long?>(null)
    var destino by mutableStateOf("")
    var esAnonima by mutableStateOf(false)
    var aceptaTerminos by mutableStateOf(false)

    var errorNombre by mutableStateOf<String?>(null)
    var errorMonto by mutableStateOf<String?>(null)
    var errorFecha by mutableStateOf<String?>(null)
    var errorDestino by mutableStateOf<String?>(null)
    var errorTerminos by mutableStateOf<String?>(null)

    var guardando by mutableStateOf(false)
        private set
    var mostrarExito by mutableStateOf(false)
        private set

    val misDonaciones = mutableStateListOf<DonacionDto>()
    var errorServidor by mutableStateOf<String?>(null)
    var errorLista by mutableStateOf<String?>(null)
    var cargandoLista by mutableStateOf(false)
        private set

    private var correoSesion: String? = null
    var datosDeCuenta by mutableStateOf(false)
        private set

    fun fechaMinimaUtc(): Long {
        val hoyLocal = Calendar.getInstance()
        val hoyUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        hoyUtc.clear()
        hoyUtc.set(
            hoyLocal.get(Calendar.YEAR),
            hoyLocal.get(Calendar.MONTH),
            hoyLocal.get(Calendar.DAY_OF_MONTH)
        )
        return hoyUtc.timeInMillis
    }

    fun actualizarMonto(valor: String) {
        if (valor.all { it.isDigit() } && valor.length <= 8) {
            monto = valor
        }
    }

    fun autorrellenar() {
        viewModelScope.launch {
            sesion.restaurarToken()
            val correoGuardado = sesion.correoActual.first()
            val usuario = if (correoGuardado != null) {
                usuarioDao.buscarPorCorreo(correoGuardado)
            } else {
                null
            }

            correoSesion = usuario?.correo
            datosDeCuenta = usuario != null

            if (usuario != null && nombre.isBlank()) {
                nombre = usuario.nombre
            }
            cargarMisDonaciones()
        }
    }

    fun formularioValido(): Boolean = validar()

    fun guardar() {
        if (!validar()) return

        viewModelScope.launch {
            val fechaElegida = fecha ?: return@launch
            guardando = true
            errorServidor = null

            val montoDonado = monto.toInt()
            val destinoElegido = destino

            val dto = DonacionDto(
                nombre = if (esAnonima) "Anónimo" else nombre.trim(),
                monto = montoDonado,
                fecha = fechaElegida,
                destino = destinoElegido,
                esAnonima = esAnonima,
                aceptaTerminos = aceptaTerminos,
                correoUsuario = if (esAnonima) null else correoSesion
            )

            val resultado = remoto.crear(dto)
            if (resultado.isFailure) {
                errorServidor = resultado.exceptionOrNull()?.mensajeAmigable()
                guardando = false
                return@launch
            }

            donacionDao.insertar(
                DonacionEntity(
                    nombre = dto.nombre,
                    monto = dto.monto,
                    fecha = dto.fecha,
                    destino = dto.destino,
                    esAnonima = dto.esAnonima,
                    aceptaTerminos = dto.aceptaTerminos,
                    correoUsuario = dto.correoUsuario
                )
            )

            val montoTexto = NumberFormat
                .getIntegerInstance(Locale.Builder().setLanguage("es").setRegion("CL").build())
                .format(montoDonado)
            Notificador.mostrarDonacion(
                context = getApplication(),
                titulo = "¡Gracias por tu aporte!",
                mensaje = "Registramos tu intención de donar \$$montoTexto para: $destinoElegido."
            )

            limpiarFormulario()
            guardando = false
            mostrarExito = true
        }
    }

    fun cerrarExito() {
        mostrarExito = false
    }


    fun cargarMisDonaciones() {
        val correo = correoSesion
        if (correo == null) {
            misDonaciones.clear()
            return
        }
        viewModelScope.launch {
            cargandoLista = true
            errorLista = null
            remoto.listarPorUsuario(correo)
                .onSuccess {
                    misDonaciones.clear()
                    misDonaciones.addAll(it)
                }
                .onFailure { errorLista = it.mensajeAmigable() }
            cargandoLista = false
        }
    }

    fun eliminarDonacion(id: Long?) {
        if (id == null) return
        viewModelScope.launch {
            remoto.eliminar(id)
                .onSuccess { cargarMisDonaciones() }
                .onFailure { errorLista = it.mensajeAmigable() }
        }
    }

    fun guardarEdicion(
        original: DonacionDto,
        nuevoMonto: String,
        nuevoDestino: String,
        alTerminar: () -> Unit
    ) {
        val id = original.id ?: return
        val montoNumero = nuevoMonto.toIntOrNull()
        if (montoNumero == null || montoNumero < MONTO_MINIMO || montoNumero > MONTO_MAXIMO) {
            errorLista = "El monto debe estar entre \$1.000 y \$1.000.000"
            return
        }
        viewModelScope.launch {
            remoto.actualizar(id, original.copy(monto = montoNumero, destino = nuevoDestino))
                .onSuccess {
                    errorLista = null
                    cargarMisDonaciones()
                    alTerminar()
                }
                .onFailure { errorLista = it.mensajeAmigable() }
        }
    }


    private fun validar(): Boolean {
        limpiarErrores()
        var valido = true

        if (!esAnonima) {
            if (nombre.isBlank()) {
                errorNombre = "El nombre es obligatorio"
                valido = false
            } else if (nombre.trim().length < 3) {
                errorNombre = "El nombre debe tener al menos 3 caracteres"
                valido = false
            }
        }

        val montoNumero = monto.toIntOrNull()
        when {
            montoNumero == null -> {
                errorMonto = "Ingresa un monto"
                valido = false
            }
            montoNumero < MONTO_MINIMO -> {
                errorMonto = "El monto mínimo es \$1.000"
                valido = false
            }
            montoNumero > MONTO_MAXIMO -> {
                errorMonto = "El monto máximo es \$1.000.000"
                valido = false
            }
        }

        val fechaElegida = fecha
        if (fechaElegida == null) {
            errorFecha = "Selecciona una fecha"
            valido = false
        } else if (fechaElegida < fechaMinimaUtc()) {
            errorFecha = "La fecha no puede ser anterior a hoy"
            valido = false
        }

        if (destino.isBlank()) {
            errorDestino = "Selecciona el destino del aporte"
            valido = false
        }

        if (!aceptaTerminos) {
            errorTerminos = "Debes aceptar los términos y condiciones"
            valido = false
        }

        return valido
    }

    private fun limpiarErrores() {
        errorNombre = null
        errorMonto = null
        errorFecha = null
        errorDestino = null
        errorTerminos = null
    }

    private fun limpiarFormulario() {
        nombre = ""
        monto = ""
        fecha = null
        destino = ""
        esAnonima = false
        aceptaTerminos = false
        limpiarErrores()
        autorrellenar()
    }
}