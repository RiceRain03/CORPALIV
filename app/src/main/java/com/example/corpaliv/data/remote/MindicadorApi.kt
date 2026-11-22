package com.example.corpaliv.data.remote

import com.example.corpaliv.data.model.DolarRespuesta
import retrofit2.http.GET

interface MindicadorApi {

    @GET("api/dolar")
    suspend fun obtenerDolar(): DolarRespuesta
}