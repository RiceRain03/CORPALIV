package com.example.corpaliv.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// ms-usuarios (8081)
interface AuthApi {
    @POST("api/auth/registro")
    suspend fun registro(@Body body: RegistroRequestDto): AuthResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto
}

interface UsuariosApi {
    @PUT("api/usuarios/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body body: UsuarioDto): UsuarioDto

    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}

// ms-donaciones (8082)
interface DonacionesApi {
    @POST("api/donaciones")
    suspend fun crear(@Body body: DonacionDto): DonacionDto

    @GET("api/donaciones/usuario/{correo}")
    suspend fun listarPorUsuario(@Path("correo") correo: String): List<DonacionDto>

    @PUT("api/donaciones/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body body: DonacionDto): DonacionDto

    @DELETE("api/donaciones/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}

// ms-contactos (8083)
interface ContactosApi {
    @POST("api/contactos")
    suspend fun crear(@Body body: ContactoDto): ContactoDto

    @GET("api/contactos")
    suspend fun listar(): List<ContactoDto>

    @PATCH("api/contactos/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body body: EstadoRequestDto): ContactoDto

    @DELETE("api/contactos/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}