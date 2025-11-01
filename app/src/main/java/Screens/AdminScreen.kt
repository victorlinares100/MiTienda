package Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import Model.Product
import Model.ProductCategory
import ViewModel.ProductViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun AdminScreen(viewModel: ProductViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val productList = uiState.productList

    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.INVIERNO) }
    var expanded by remember { mutableStateOf(false) }

    var editingProductId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (editingProductId == null) "Agregar Producto" else "Editar Producto",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory.name.lowercase().replaceFirstChar { it.uppercase() },
                onValueChange = {},
                label = { Text("Categoría") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Seleccionar Categoría",
                        Modifier.clickable { expanded = true }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                ProductCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val price = priceText.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank() && price > 0.0) {
                    if (editingProductId == null) {
                        // Crear nuevo
                        viewModel.addProduct(name.trim(), price, description.trim(), selectedCategory)
                    } else {
                        // Actualizar existente
                        val updated = Product(
                            id = editingProductId!!,
                            name = name.trim(),
                            price = price,
                            description = description.trim(),
                            category = selectedCategory
                        )
                        viewModel.updateProduct(updated)
                        editingProductId = null
                    }
                    name = ""
                    priceText = ""
                    description = ""
                    selectedCategory = ProductCategory.INVIERNO
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingProductId == null) "Agregar Producto" else "Actualizar Producto")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Inventario", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(productList) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text("Precio: $${product.price}")
                            Text("Categoría: ${product.category}")
                            Text("Descripción: ${product.description}")
                        }

                        Row {
                            IconButton(onClick = {
                                editingProductId = product.id
                                name = product.name
                                priceText = product.price.toString()
                                description = product.description
                                selectedCategory = product.category
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            IconButton(onClick = { viewModel.deleteProduct(product) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
