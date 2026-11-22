package com.example.corpaliv.data.repository

import com.example.corpaliv.data.model.ValorDolar
import com.example.corpaliv.data.remote.MindicadorApi
import com.example.corpaliv.data.remote.RetrofitClient

class IndicadorRepository(
    private val api: MindicadorApi = RetrofitClient.mindicador
) {

    suspend fun valorDolar(): Result<ValorDolar> {
        return try {
            val respuesta = api.obtenerDolar()
            val ultimo = respuesta.serie.maxByOrNull { it.fecha }
                ?: return Result.failure(Exception("La respuesta no trae valores"))
            Result.success(ValorDolar(valor = ultimo.valor, fecha = ultimo.fecha))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}