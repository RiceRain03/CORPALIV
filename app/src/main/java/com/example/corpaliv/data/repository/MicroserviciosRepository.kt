package com.example.corpaliv.data.repository

import com.example.corpaliv.data.remote.AuthApi
import com.example.corpaliv.data.remote.AuthResponseDto
import com.example.corpaliv.data.remote.ContactoDto
import com.example.corpaliv.data.remote.ContactosApi
import com.example.corpaliv.data.remote.DonacionDto
import com.example.corpaliv.data.remote.DonacionesApi
import com.example.corpaliv.data.remote.EstadoRequestDto
import com.example.corpaliv.data.remote.LoginRequestDto
import com.example.corpaliv.data.remote.MicroserviciosClient
import com.example.corpaliv.data.remote.RegistroRequestDto
import com.example.corpaliv.data.remote.TokenStore
import com.example.corpaliv.data.remote.UsuarioDto
import com.example.corpaliv.data.remote.UsuariosApi
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

fun Throwable.mensajeAmigable(): String = when (this) {
    is HttpException -> {
        val cuerpo = runCatching { response()?.errorBody()?.string() }.getOrNull()
        val mensajeServidor = runCatching {
            JsonParser.parseString(cuerpo).asJsonObject.get("error")?.asString
        }.getOrNull()
        mensajeServidor ?: when (code()) {
            400 -> "Datos inválidos. Revisa el formulario."
            401, 403 -> "Sin permisos o sesión expirada. Inicia sesión de nuevo."
            404 -> "No se encontró el recurso."
            else -> "Error del servidor (${code()})."
        }
    }
    is IOException -> "No se pudo conectar con el servidor."
    else -> message ?: "Error inesperado."
}

class AuthRepository(private val api: AuthApi = MicroserviciosClient.auth) {

    suspend fun login(correo: String, clave: String): Result<AuthResponseDto> =
        runCatching { api.login(LoginRequestDto(correo, clave)) }
            .onSuccess { TokenStore.token = it.token; TokenStore.rol = it.rol }

    suspend fun registrar(nombre: String, correo: String, clave: String): Result<AuthResponseDto> =
        runCatching { api.registro(RegistroRequestDto(nombre, correo, clave)) }
            .onSuccess { TokenStore.token = it.token; TokenStore.rol = it.rol }

    fun cerrarSesion() {
        TokenStore.token = null
        TokenStore.rol = null
    }
}

class UsuarioRemotoRepository(private val api: UsuariosApi = MicroserviciosClient.usuarios) {
    suspend fun actualizarNombre(id: Long, nombre: String, correo: String) =
        runCatching { api.actualizar(id, UsuarioDto(nombre = nombre, correo = correo)) }

    suspend fun eliminar(id: Long) = runCatching { api.eliminar(id) }
}

class ContactoRemotoRepository(private val api: ContactosApi = MicroserviciosClient.contactos) {
    suspend fun crear(c: ContactoDto) = runCatching { api.crear(c) }
    suspend fun listar() = runCatching { api.listar() }
    suspend fun cambiarEstado(id: Long, estado: String) =
        runCatching { api.cambiarEstado(id, EstadoRequestDto(estado)) }
    suspend fun eliminar(id: Long) = runCatching { api.eliminar(id) }
}

class DonacionRemotaRepository(private val api: DonacionesApi = MicroserviciosClient.donaciones) {
    suspend fun crear(d: DonacionDto) = runCatching { api.crear(d) }
    suspend fun listarPorUsuario(correo: String) = runCatching { api.listarPorUsuario(correo) }
    suspend fun actualizar(id: Long, d: DonacionDto) = runCatching { api.actualizar(id, d) }
    suspend fun eliminar(id: Long) = runCatching { api.eliminar(id) }
}