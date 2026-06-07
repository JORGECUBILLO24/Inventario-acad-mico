package com.example.inventarioacademico.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventarioDao {

    // === MÓDULO 1: GESTIÓN DE EQUIPOS ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: Equipo)

    @Update
    suspend fun updateEquipo(equipo: Equipo)

    @Delete
    suspend fun deleteEquipo(equipo: Equipo)

    @Query("SELECT * FROM equipos ORDER BY nombre ASC")
    fun getAllEquipos(): Flow<List<Equipo>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun getEquipoById(id: Int): Equipo?

    @Query("SELECT * FROM equipos WHERE nombre LIKE '%' || :query || '%' OR numeroSerie LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchEquipos(query: String): Flow<List<Equipo>>

    @Query("SELECT * FROM equipos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun getEquiposByCategoria(categoria: String): Flow<List<Equipo>>

    @Query("SELECT categoria FROM equipos GROUP BY categoria ORDER BY COUNT(*) DESC LIMIT 1")
    fun getCategoriaMasFrecuente(): Flow<String?>


    // === MÓDULO 2: GESTIÓN DE PRÉSTAMOS ===
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrestamo(prestamo: Prestamo)

    @Update
    suspend fun updatePrestamo(prestamo: Prestamo)

    @Query("SELECT * FROM prestamos ORDER BY id DESC")
    fun getAllPrestamos(): Flow<List<Prestamo>>


    // === MÓDULO 3: DASHBOARD (Conteos y Estadísticas) ===
    @Query("SELECT COUNT(*) FROM equipos")
    fun getTotalEquipos(): Flow<Int>

    @Query("SELECT COUNT(*) FROM equipos WHERE disponible = 1")
    fun getEquiposDisponibles(): Flow<Int>

    @Query("SELECT COUNT(*) FROM equipos WHERE disponible = 0")
    fun getEquiposPrestados(): Flow<Int>
}