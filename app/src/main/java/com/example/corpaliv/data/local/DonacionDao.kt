package com.example.corpaliv.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DonacionDao {
    @Insert
    suspend fun insertar(donacion: DonacionEntity)

    @Update
    suspend fun actualizar(donacion: DonacionEntity)

    @Delete
    suspend fun eliminar(donacion: DonacionEntity)

    @Query("SELECT * FROM donaciones ORDER BY id DESC")
    fun obtenerDonaciones(): Flow<List<DonacionEntity>>
}