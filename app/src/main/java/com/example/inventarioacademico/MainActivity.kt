package com.example.inventarioacademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventarioacademico.data.InventarioDatabase
import com.example.inventarioacademico.repository.InventarioRepository
import com.example.inventarioacademico.ui.ui.AppNavigation
import com.example.inventarioacademico.ui.theme.InventarioAcademicoTheme
// ¡ESTA ES LA IMPORTACIÓN QUE FALTABA!
import com.example.inventarioacademico.ui.ui.InventarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializar la Base de Datos Room
        val database = InventarioDatabase.getDatabase(this)

        // 2. Inicializar el Repositorio pasando el DAO
        val repository = InventarioRepository(database.inventarioDao())

        // 3. Crear el Factory para instanciar el ViewModel con el repositorio
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InventarioViewModel(repository) as T
            }
        }

        setContent {
            InventarioAcademicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 4. Instanciar el ViewModel
                    val viewModel: InventarioViewModel = viewModel(factory = factory)

                    // 5. Lanzar la estructura de pantallas
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}