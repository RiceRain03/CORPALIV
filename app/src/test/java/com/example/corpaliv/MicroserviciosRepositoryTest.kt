package com.example.corpaliv

import com.example.corpaliv.data.remote.AuthApi
import com.example.corpaliv.data.remote.AuthResponseDto
import com.example.corpaliv.data.remote.ContactoDto
import com.example.corpaliv.data.remote.ContactosApi
import com.example.corpaliv.data.remote.DonacionesApi
import com.example.corpaliv.data.remote.EstadoRequestDto
import com.example.corpaliv.data.remote.TokenStore
import com.example.corpaliv.data.repository.AuthRepository
import com.example.corpaliv.data.repository.ContactoRemotoRepository
import com.example.corpaliv.data.repository.DonacionRemotaRepository
import com.example.corpaliv.data.repository.mensajeAmigable
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class MicroserviciosRepositoryTest {

    @Before
    fun limpiar() {
        TokenStore.token = null
        TokenStore.rol = null
    }

    @Test
    fun `login exitoso guarda token y rol`() = runTest {
        val api = mockk<AuthApi>()
        coEvery { api.login(any()) } returns AuthResponseDto("tok123", 1L, "Ana", "ana@test.cl", "ADMIN")

        val resultado = AuthRepository(api).login("ana@test.cl", "123456")

        assertTrue(resultado.isSuccess)
        assertEquals("tok123", TokenStore.token)
        assertEquals("ADMIN", TokenStore.rol)
    }

    @Test
    fun `login fallido no guarda token`() = runTest {
        val api = mockk<AuthApi>()
        coEvery { api.login(any()) } throws IOException("sin red")

        val resultado = AuthRepository(api).login("ana@test.cl", "mala")

        assertTrue(resultado.isFailure)
        assertNull(TokenStore.token)
    }

    @Test
    fun `cerrar sesion limpia el token`() {
        TokenStore.token = "abc"
        TokenStore.rol = "USER"

        AuthRepository(mockk()).cerrarSesion()

        assertNull(TokenStore.token)
        assertNull(TokenStore.rol)
    }

    @Test
    fun `crear contacto envia el dto a la api`() = runTest {
        val api = mockk<ContactosApi>()
        val dto = ContactoDto(
            nombre = "Empresa X", edad = 30, fechaContacto = "2026-11-23",
            esEmpresa = true, aceptaTerminos = true, mensaje = "Hola"
        )
        coEvery { api.crear(dto) } returns dto.copy(id = 7L, estado = "RECIBIDA")

        val resultado = ContactoRemotoRepository(api).crear(dto)

        assertEquals("RECIBIDA", resultado.getOrNull()?.estado)
        coVerify(exactly = 1) { api.crear(dto) }
    }

    @Test
    fun `cambiar estado llama al endpoint con el estado nuevo`() = runTest {
        val api = mockk<ContactosApi>()
        val dto = ContactoDto(
            id = 5L, nombre = "Ana", edad = 20, fechaContacto = "2026-11-23",
            esEmpresa = false, aceptaTerminos = true, estado = "CONTACTADO"
        )
        coEvery { api.cambiarEstado(5L, EstadoRequestDto("CONTACTADO")) } returns dto

        val resultado = ContactoRemotoRepository(api).cambiarEstado(5L, "CONTACTADO")

        assertEquals("CONTACTADO", resultado.getOrNull()?.estado)
    }

    @Test
    fun `eliminar donacion con error de red devuelve failure`() = runTest {
        val api = mockk<DonacionesApi>()
        coEvery { api.eliminar(3L) } throws IOException("sin red")

        val resultado = DonacionRemotaRepository(api).eliminar(3L)

        assertTrue(resultado.isFailure)
    }

    @Test
    fun `error de red muestra mensaje amigable`() {
        assertEquals("No se pudo conectar con el servidor.", IOException().mensajeAmigable())
    }
}