package com.example.inventarioacademico.ui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamosScreen(viewModel: InventarioViewModel) {
    val prestamos by viewModel.prestamosList.collectAsState()
    val disponibles by viewModel.equiposDisponiblesParaPrestamo.collectAsState()
    
    var showPrestamoDialog by remember { mutableStateOf(false) }
    var equipoSeleccionado by remember { mutableStateOf<com.example.inventarioacademico.data.Equipo?>(null) }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("🤝 Gestión de Préstamos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Sección: Equipos para Prestar
        Text("Equipos Disponibles", style = MaterialTheme.typography.titleMedium)
        if (disponibles.isEmpty()) {
            Text("No hay equipos disponibles para préstamo.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(disponibles) { equipo ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = {
                            equipoSeleccionado = equipo
                            showPrestamoDialog = true
                        }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(equipo.nombre)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Prestar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Sección: Historial y Préstamos Activos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Historial de Movimientos", style = MaterialTheme.typography.titleMedium)
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(prestamos) { prestamo ->
                PrestamoItem(
                    prestamo = prestamo,
                    onDevolver = { viewModel.devolverEquipo(prestamo) }
                )
            }
        }
    }

    if (showPrestamoDialog && equipoSeleccionado != null) {
        RegistrarPrestamoDialog(
            equipoNombre = equipoSeleccionado!!.nombre,
            onDismiss = { showPrestamoDialog = false },
            onConfirm = { solicitante ->
                viewModel.realizarPrestamo(equipoSeleccionado!!, solicitante)
                showPrestamoDialog = false
            }
        )
    }
}

@Composable
fun PrestamoItem(prestamo: com.example.inventarioacademico.data.Prestamo, onDevolver: () -> Unit) {
    val esDevuelto = prestamo.fechaDevolucion != null
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esDevuelto) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Equipo ID: ${prestamo.equipoId}", fontWeight = FontWeight.Bold)
                Text("Solicitante: ${prestamo.solicitante}")
                Text("Prestado: ${prestamo.fechaPrestamo}", style = MaterialTheme.typography.labelSmall)
                if (esDevuelto) {
                    Text("Devuelto: ${prestamo.fechaDevolucion}", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                }
            }
            if (!esDevuelto) {
                Button(onClick = onDevolver, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Devolver", fontSize = 12.sp)
                }
            } else {
                Text("Completado", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun RegistrarPrestamoDialog(equipoNombre: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var solicitante by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Préstamo") },
        text = {
            Column {
                Text("Equipo: $equipoNombre", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = solicitante,
                    onValueChange = { solicitante = it },
                    label = { Text("Nombre del Solicitante") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (solicitante.isNotBlank()) onConfirm(solicitante) }) {
                Text("Confirmar Préstamo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
