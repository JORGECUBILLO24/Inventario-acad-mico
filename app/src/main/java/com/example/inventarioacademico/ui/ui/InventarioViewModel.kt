package com.example.inventarioacademico.ui.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventarioacademico.data.Equipo
import com.example.inventarioacademico.repository.InventarioRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventarioViewModel(private val repository: InventarioRepository) : ViewModel() {

    // === ESTADO DE AUTENTICACIÓN LOCAL ===
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun verificarPassword(password: String): Boolean {
        return if (password == "admin123") { // Contraseña local por defecto
            _isLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun cerrarSesion() { _isLoggedIn.value = false }

    // === ESTADOS PARA PRÉSTAMOS ===
    val prestamosList: StateFlow<List<com.example.inventarioacademico.data.Prestamo>> = repository.allPrestamos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val equiposDisponiblesParaPrestamo: StateFlow<List<Equipo>> = repository.allEquipos
        .map { lista -> lista.filter { it.disponible } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === ESTADOS PARA BÚSQUEDA Y FILTROS ===
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _categoriaFiltro = MutableStateFlow("")
    val categoriaFiltro = _categoriaFiltro.asStateFlow()

    val categoriasDisponibles: StateFlow<List<String>> = repository.allEquipos.map { lista ->
        lista.map { it.categoria }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val equiposList: StateFlow<List<Equipo>> = combine(
        repository.allEquipos,
        _searchQuery,
        _categoriaFiltro
    ) { lista, query, categoria ->
        lista.filter { equipo ->
            val coincideTexto = equipo.nombre.contains(query, ignoreCase = true) ||
                    equipo.numeroSerie.contains(query, ignoreCase = true)
            val coincideCategoria = categoria.isEmpty() || equipo.categoria.equals(categoria, ignoreCase = true)
            coincideTexto && coincideCategoria
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === ESTADÍSTICAS PARA EL DASHBOARD Y GRÁFICOS ===
    val totalEquipos = repository.totalEquipos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val disponibles = repository.equiposDisponibles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val prestados = repository.equiposPrestados.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Mapa de datos ordenados para generar los gráficos por categoría
    val datosGrafico: StateFlow<Map<String, Int>> = repository.allEquipos.map { lista ->
        lista.groupingBy { it.categoria }.eachCount()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val categoriaTop: StateFlow<String> = repository.allEquipos.map { lista ->
        if (lista.isEmpty()) "Sin datos"
        else lista.groupingBy { it.categoria }.eachCount().maxByOrNull { it.value }?.key ?: "N/A"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Cargando...")

    fun actualizarBusqueda(query: String) { _searchQuery.value = query }
    fun actualizarCategoria(categoria: String) {
        _categoriaFiltro.value = if (_categoriaFiltro.value == categoria) "" else categoria
    }

    // === FUNCIONES CRUD ===
    fun agregarEquipo(nombre: String, cat: String, marca: String, serie: String, uri: String? = null) {
        viewModelScope.launch {
            repository.registrarEquipo(Equipo(
                nombre = nombre, 
                categoria = cat, 
                marca = marca, 
                numeroSerie = serie,
                imagenUri = uri
            ))
        }
    }

    fun eliminarEquipo(equipo: Equipo) {
        viewModelScope.launch { repository.eliminarEquipo(equipo) }
    }

    // === ACCIONES DE PRÉSTAMOS ===
    fun realizarPrestamo(equipo: Equipo, solicitante: String) {
        viewModelScope.launch {
            val fecha = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
            repository.registrarPrestamo(com.example.inventarioacademico.data.Prestamo(
                equipoId = equipo.id,
                solicitante = solicitante,
                fechaPrestamo = fecha
            ))
        }
    }

    fun devolverEquipo(prestamo: com.example.inventarioacademico.data.Prestamo) {
        viewModelScope.launch {
            val fecha = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
            repository.registrarDevolucion(prestamo, fecha)
        }
    }

    // === GENERACIÓN DE INFORMACIÓN CSV ===
    fun obtenerTextoCSV(): String {
        val lista = equiposList.value
        val csv = StringBuilder()
        csv.append("ID,Nombre,Categoria,Marca,NumeroSerie,Disponible\n")
        for (e in lista) {
            csv.append("${e.id},${e.nombre},${e.categoria},${e.marca},${e.numeroSerie},${e.disponible}\n")
        }
        return csv.toString()
    }
}