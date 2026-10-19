package com.example.corpaliv.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {
    @Insert
    suspend fun insertarContacto(contacto: ContactoEntity)

    @Query("SELECT * FROM contactos ORDER BY id DESC")
    fun obtenerContactos(): Flow<List<ContactoEntity>>
}