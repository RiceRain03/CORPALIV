package com.example.corpaliv.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sesion")

class SesionDataStore(private val context: Context) {

    companion object {
        private val CORREO_ACTUAL = stringPreferencesKey("correo_actual")
    }
    val correoActual: Flow<String?> =
        context.dataStore.data.map { preferencias -> preferencias[CORREO_ACTUAL] }

    suspend fun guardarSesion(correo: String) {
        context.dataStore.edit { preferencias -> preferencias[CORREO_ACTUAL] = correo }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferencias -> preferencias.remove(CORREO_ACTUAL) }
    }
}