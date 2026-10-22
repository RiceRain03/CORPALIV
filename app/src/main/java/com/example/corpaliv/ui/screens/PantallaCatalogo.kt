package com.example.corpaliv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

data class ItemCatalogo(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val precio: String,
    val detalleCompleto: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    modifier: Modifier = Modifier
) {
    val listaProductos = remember {
        listOf(
            ItemCatalogo(
                id = 1,
                titulo = "Manual de Lenguaje Señas",
                descripcion = "Guía básica e ilustrada de comunicación.",
                categoria = "Educación",
                precio = "$8.000",
                detalleCompleto = "Manual con ilustraciones paso a paso para aprender las señas y símbolos fundamentales de comunicación e inclusión."
            ),
            ItemCatalogo(
                id = 2,
                titulo = "Taza Causa Corpaliv",
                descripcion = "Taza de cerámica con diseño exclusivo.",
                categoria = "Merchandising",
                precio = "$5.500",
                detalleCompleto = "Taza de 350ml hecha con materiales sostenibles. Todo lo recaudado va en beneficio directo de los talleres inclusivos."
            ),
            ItemCatalogo(
                id = 3,
                titulo = "Artesanía en Cerámica",
                descripcion = "Elaborada en talleres de estimulación.",
                categoria = "Artesanías",
                precio = "$12.000",
                detalleCompleto = "Pieza única moldeada a mano por participantes de los programas de la fundación durante sus sesiones terapéuticas."
            ),
            ItemCatalogo(
                id = 4,
                titulo = "Set de Tarjetas Táctiles",
                descripcion = "Material didáctico multisensorial.",
                categoria = "Educación",
                precio = "$15.000",
                detalleCompleto = "Set de 20 tarjetas con relieves y texturas variadas diseñadas para el apoyo al desarrollo motriz y sensorial."
            ),
            ItemCatalogo(
                id = 5,
                titulo = "Bolsa Ecológica Tela",
                descripcion = "Bolsa reutilizable 100% algodón.",
                categoria = "Merchandising",
                precio = "$4.000",
                detalleCompleto = "Bolsa resistente e ilustrada a mano. Ideal para compras del día a día promocionando la inclusión."
            ),
            ItemCatalogo(
                id = 6,
                titulo = "Calendario Ilustrado 2026",
                descripcion = "Con historias y fotos del proyecto.",
                categoria = "Artesanías",
                precio = "$6.000",
                detalleCompleto = "Calendario anual que destaca cada mes la historia de superación de un participante y sus logros en la comunidad."
            )
        )
    }

    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var menuCategoriasExpandido by remember { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<ItemCatalogo?>(null) }

    val categorias = listOf("Todos", "Educación", "Merchandising", "Artesanías")

    val productosFiltrados = listaProductos.filter { item ->
        val coincideBusqueda = item.titulo.contains(textoBusqueda, ignoreCase = true) ||
                item.descripcion.contains(textoBusqueda, ignoreCase = true)
        val coincideCategoria =
            categoriaSeleccionada == "Todos" || item.categoria == categoriaSeleccionada
        coincideBusqueda && coincideCategoria
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Catálogo de Productos y Soluciones",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 1. Campo de Búsqueda
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar en el catálogo...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = menuCategoriasExpandido,
            onExpandedChange = { menuCategoriasExpandido = !menuCategoriasExpandido },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrar por Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategoriasExpandido) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuCategoriasExpandido,
                onDismissRequest = { menuCategoriasExpandido = false }
            ) {
                categorias.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            categoriaSeleccionada = cat
                            menuCategoriasExpandido = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedContent(
            targetState = categoriaSeleccionada,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "TransicionCatalogo"
        ) { categoria ->
            if (productosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron productos en esta categoría.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productosFiltrados) { item ->
                        Card(
                            onClick = { productoSeleccionado = item },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = item.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.descripcion,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.precio,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            productoSeleccionado?.let { item ->
                AlertDialog(
                    onDismissRequest = { productoSeleccionado = null },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    title = { Text(text = item.titulo) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Categoría: ${item.categoria}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(text = item.detalleCompleto)
                            Text(
                                text = "Valor aporte sugerido: ${item.precio}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { productoSeleccionado = null }) {
                            Text("Cerrar")
                        }
                    }
                )
            }
        }
    }
}