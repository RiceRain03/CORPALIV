package com.example.corpaliv.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.corpaliv.data.remote.TokenStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sesion")

class SesionDataStore(private val context: Context) {

    companion object {
        private val CORREO_ACTUAL = stringPreferencesKey("correo_actual")
        private val TOKEN = stringPreferencesKey("token")
        private val ROL = stringPreferencesKey("rol")
        private val ID_REMOTO = longPreferencesKey("id_remoto")
    }

    val correoActual: Flow<String?> =
        context.dataStore.data.map { preferencias -> preferencias[CORREO_ACTUAL] }

    val idRemoto: Flow<Long?> =
        context.dataStore.data.map { preferencias -> preferencias[ID_REMOTO] }

    suspend fun guardarSesion(correo: String) {
        context.dataStore.edit { preferencias -> preferencias[CORREO_ACTUAL] = correo }
    }

    suspend fun guardarCredenciales(token: String, rol: String, idRemoto: Long) {
        TokenStore.token = token
        TokenStore.rol = rol
        context.dataStore.edit { preferencias ->
            preferencias[TOKEN] = token
            preferencias[ROL] = rol
            preferencias[ID_REMOTO] = idRemoto
        }
    }

    /** Si la app se reabrió, recupera el token guardado a memoria. */
    suspend fun restaurarToken() {
        if (TokenStore.token != null) return
        val preferencias = context.dataStore.data.first()
        TokenStore.token = preferencias[TOKEN]
        TokenStore.rol = preferencias[ROL]
    }

    suspend fun cerrarSesion() {
        TokenStore.token = null
        TokenStore.rol = null
        context.dataStore.edit { preferencias ->
            preferencias.remove(CORREO_ACTUAL)
            preferencias.remove(TOKEN)
            preferencias.remove(ROL)
            preferencias.remove(ID_REMOTO)
        }
    }
}