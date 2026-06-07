package com.example.inventarioacademico.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class Equipo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val marca: String,
    val numeroSerie: String,
    val disponible: Boolean = true,
    val imagenUri: String? = null // URI de la imagen seleccionada
)

@Entity(tableName = "prestamos")
data class Prestamo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val solicitante: String,
    val fechaPrestamo: String,
    val fechaDevolucion: String? = null // 'null' significa que el equipo aún está prestado
)