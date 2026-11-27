package com.example.corpaliv.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object TokenStore {
    @Volatile var token: String? = null
    @Volatile var rol: String? = null
}

object MicroserviciosClient {

    private const val HOST = "http://10.0.2.2"

    private val interceptorToken = Interceptor { cadena ->
        val peticion = cadena.request().newBuilder().apply {
            TokenStore.token?.let { header("Authorization", "Bearer $it") }
        }.build()
        cadena.proceed(peticion)
    }

    private val registro = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(interceptorToken)
        .addInterceptor(registro)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun <T> crear(puerto: Int, api: Class<T>): T =
        Retrofit.Builder()
            .baseUrl("$HOST:$puerto/")
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)

    val auth: AuthApi by lazy { crear(8081, AuthApi::class.java) }
    val usuarios: UsuariosApi by lazy { crear(8081, UsuariosApi::class.java) }
    val donaciones: DonacionesApi by lazy { crear(8082, DonacionesApi::class.java) }
    val contactos: ContactosApi by lazy { crear(8083, ContactosApi::class.java) }
}