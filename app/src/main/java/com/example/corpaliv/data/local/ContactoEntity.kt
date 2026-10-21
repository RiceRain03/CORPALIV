package com.example.corpaliv.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contactos")
data class ContactoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val edad: Int,
    val fechaContacto: String,
    val esEmpresa: Boolean,
    val aceptaTerminos: Boolean,
    val mensaje: String
)
