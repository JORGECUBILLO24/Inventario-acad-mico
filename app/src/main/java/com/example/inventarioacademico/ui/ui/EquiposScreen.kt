package com.example.inventarioacademico.ui.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.inventarioacademico.data.Equipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquiposScreen(viewModel: InventarioViewModel) {
    val equipos by viewModel.equiposList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categoriaFiltro by viewModel.categoriaFiltro.collectAsState()
    val categorias by viewModel.categoriasDisponibles.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var equipoAEditar by remember { mutableStateOf<Equipo?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Equipo", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📦 Inventario Pro", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                
                IconButton(onClick = {
                    val csvData = viewModel.obtenerTextoCSV()
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_SUBJECT, "Inventario_Equipos.csv")
                        putExtra(Intent.EXTRA_TEXT, csvData)
                    }
                    context.startActivity(Intent.createChooser(intent, "Exportar a Excel (CSV)"))
                }) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Exportar", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.actualizarBusqueda(it) },
                placeholder = { Text("Buscar por nombre, serie o marca...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )

            if (categorias.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categorias) { cat ->
                        FilterChip(
                            selected = cat == categoriaFiltro,
                            onClick = { viewModel.actualizarCategoria(cat) },
                            label = { Text(cat) },
                            leadingIcon = if (cat == categoriaFiltro) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            shape = CircleShape
                        )
                    }
                }
            }

            if (equipos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Text("No hay equipos registrados", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(equipos) { equipo ->
                        EquipoCard(
                            equipo = equipo,
                            onEdit = { equipoAEditar = equipo },
                            onDelete = { viewModel.eliminarEquipo(equipo) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditEquipoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { n, c, m, s, u ->
                viewModel.agregarEquipo(n, c, m, s, u)
                showAddDialog = false
            }
        )
    }

    if (equipoAEditar != null) {
        AddEditEquipoDialog(
            equipo = equipoAEditar,
            onDismiss = { equipoAEditar = null },
            onConfirm = { n, c, m, s, u ->
                viewModel.editarEquipo(equipoAEditar!!, n, c, m, s, u)
                equipoAEditar = null
            }
        )
    }
}

@Composable
fun EquipoCard(equipo: Equipo, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (equipo.imagenUri != null) {
                    AsyncImage(
                        model = equipo.imagenUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(equipo.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(equipo.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (equipo.disponible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (equipo.disponible) "Disponible" else "Prestado",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (equipo.disponible) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("S/N: ${equipo.numeroSerie}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun AddEditEquipoDialog(
    equipo: Equipo? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String?) -> Unit
) {
    var nombre by remember { mutableStateOf(equipo?.nombre ?: "") }
    var categoria by remember { mutableStateOf(equipo?.categoria ?: "") }
    var marca by remember { mutableStateOf(equipo?.marca ?: "") }
    var serie by remember { mutableStateOf(equipo?.numeroSerie ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(equipo?.imagenUri?.let { Uri.parse(it) }) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (equipo == null) "Nuevo Equipo" else "Editar Equipo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null)
                            Text("Foto", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = serie, onValueChange = { serie = it }, label = { Text("Número de Serie") }, shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = { if (nombre.isNotBlank() && categoria.isNotBlank()) onConfirm(nombre, categoria, marca, serie, imageUri?.toString()) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (equipo == null) "Registrar" else "Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
