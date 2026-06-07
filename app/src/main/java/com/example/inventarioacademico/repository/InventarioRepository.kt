package com.example.inventarioacademico.repository

import com.example.inventarioacademico.data.Equipo
import com.example.inventarioacademico.data.InventarioDao
import com.example.inventarioacademico.data.Prestamo
import kotlinx.coroutines.flow.Flow

class InventarioRepository(private val dao: InventarioDao) {

    // Flujos de datos en tiempo real (Flow) para la UI
    val allEquipos: Flow<List<Equipo>> = dao.getAllEquipos()
    val allPrestamos: Flow<List<Prestamo>> = dao.getAllPrestamos()

    val totalEquipos: Flow<Int> = dao.getTotalEquipos()
    val equiposDisponibles: Flow<Int> = dao.getEquiposDisponibles()
    val equiposPrestados: Flow<Int> = dao.getEquiposPrestados()
    val categoriaMasFrecuente: Flow<String?> = dao.getCategoriaMasFrecuente()

    // Métodos para Equipos
    suspend fun registrarEquipo(equipo: Equipo) = dao.insertEquipo(equipo)
    suspend fun actualizarEquipo(equipo: Equipo) = dao.updateEquipo(equipo)
    suspend fun eliminarEquipo(equipo: Equipo) = dao.deleteEquipo(equipo)
    suspend fun obtenerEquipoPorId(id: Int): Equipo? = dao.getEquipoById(id)

    fun buscarEquipos(query: String): Flow<List<Equipo>> = dao.searchEquipos(query)
    fun filtrarPorCategoria(categoria: String): Flow<List<Equipo>> = dao.getEquiposByCategoria(categoria)

    // Métodos para Préstamos
    suspend fun registrarPrestamo(prestamo: Prestamo) {
        dao.insertPrestamo(prestamo)

        // Lógica Automática: Si prestamos un equipo, cambiamos su estado a NO disponible
        val equipo = dao.getEquipoById(prestamo.equipoId)
        if (equipo != null) {
            dao.updateEquipo(equipo.copy(disponible = false))
        }
    }

    suspend fun registrarDevolucion(prestamo: Prestamo, fechaDevolucion: String) {
        // Actualizamos el registro del préstamo con su fecha de entrega
        dao.updatePrestamo(prestamo.copy(fechaDevolucion = fechaDevolucion))

        // Lógica Automática: El equipo vuelve a estar disponible
        val equipo = dao.getEquipoById(prestamo.equipoId)
        if (equipo != null) {
            dao.updateEquipo(equipo.copy(disponible = true))
        }
    }
}