package com.example.corpaliv.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val URL_MINDICADOR = "https://mindicador.cl/"

    // Muestra cada petición y respuesta en el Logcat (filtra por "OkHttp")
    private val registro = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(registro)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val mindicador: MindicadorApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_MINDICADOR)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindicadorApi::class.java)
    }
}