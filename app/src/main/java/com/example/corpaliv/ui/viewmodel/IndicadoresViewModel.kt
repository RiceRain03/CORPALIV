package com.example.corpaliv.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpaliv.data.model.ValorDolar
import com.example.corpaliv.data.repository.IndicadorRepository
import kotlinx.coroutines.launch

class IndicadoresViewModel(
    private val repositorio: IndicadorRepository = IndicadorRepository()
) : ViewModel() {

    var dolar by mutableStateOf<ValorDolar?>(null)
        private set
    var cargando by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            cargando = true
            error = null
            repositorio.valorDolar()
                .onSuccess { dolar = it }
                .onFailure { error = "No se pudo consultar el dólar. Revisa tu conexión." }
            cargando = false
        }
    }

    fun clpAUsd(clp: Int): Double? {
        val valorDolar = dolar?.valor ?: return null
        return clp / valorDolar
    }
}