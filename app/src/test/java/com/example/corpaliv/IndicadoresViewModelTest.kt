package com.example.corpaliv

import com.example.corpaliv.data.model.DolarRespuesta
import com.example.corpaliv.data.model.ValorIndicador
import com.example.corpaliv.data.remote.MindicadorApi
import com.example.corpaliv.data.repository.IndicadorRepository
import com.example.corpaliv.ui.viewmodel.IndicadoresViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IndicadoresViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun clpAUsd_convierteCorrectamenteConUltimoValorDolar() {
        val apiFalsa = object : MindicadorApi {
            override suspend fun obtenerDolar(): DolarRespuesta {
                return DolarRespuesta(
                    codigo = "dolar",
                    nombre = "Dólar observado",
                    serie = listOf(ValorIndicador(fecha = "2026-11-23T00:00:00.000Z", valor = 950.0)),
                )
            }
        }

        val viewModel = IndicadoresViewModel(IndicadorRepository(apiFalsa))
        val resultadoUsd = viewModel.clpAUsd(9500)

        assertNotNull(resultadoUsd)
        assertEquals(10.0, resultadoUsd!!, 0.01)
    }

    @Test
    fun cargar_manejaErrorDeRedCorrectamente() {
        val apiConError = object : MindicadorApi {
            override suspend fun obtenerDolar(): DolarRespuesta {
                throw RuntimeException("Sin conexión")
            }
        }

        val viewModel = IndicadoresViewModel(IndicadorRepository(apiConError))

        assertNull(viewModel.dolar)
        assertNotNull(viewModel.error)
    }
}
