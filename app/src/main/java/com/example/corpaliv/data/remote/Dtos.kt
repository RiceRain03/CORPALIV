package com.example.corpaliv.data.remote

data class LoginRequestDto(val correo: String, val clave: String)

data class RegistroRequestDto(val nombre: String, val correo: String, val clave: String)

data class AuthResponseDto(
    val token: String,
    val id: Long,
    val nombre: String,
    val correo: String,
    val rol: String
)

data class UsuarioDto(
    val id: Long? = null,
    val nombre: String,
    val correo: String? = null
)

data class ContactoDto(
    val id: Long? = null,
    val nombre: String,
    val edad: Int,
    val fechaContacto: String,
    val esEmpresa: Boolean,
    val aceptaTerminos: Boolean,
    val mensaje: String? = null,
    val tipoColaboracion: String? = null,
    val estado: String? = null
)

data class EstadoRequestDto(val estado: String)

data class DonacionDto(
    val id: Long? = null,
    val nombre: String,
    val monto: Int,
    val fecha: Long,
    val destino: String,
    val esAnonima: Boolean,
    val aceptaTerminos: Boolean,
    val correoUsuario: String? = null
)