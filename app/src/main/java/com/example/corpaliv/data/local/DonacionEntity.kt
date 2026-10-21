package com.example.corpaliv.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donaciones")
data class DonacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val monto: Int,
    val fecha: Long,
    val destino: String,
    val esAnonima: Boolean,
    val aceptaTerminos: Boolean,
    val correoUsuario: String? = null
)