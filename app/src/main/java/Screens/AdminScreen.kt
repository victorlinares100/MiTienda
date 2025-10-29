package Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.ProductCategory
import com.example.mitienda.Product
import com.example.mitienda.ProductViewModel
import androidx.compose.runtime.collectAsState // Importante para StateFlow

@Composable
fun AdminScreen(viewModel: ProductViewModel) {

    // CAMBIO CRUCIAL: Observar el StateFlow de la base de datos
    val uiState by viewModel.uiState.collectAsState()
    val productList = uiState.productList // La lista persistente

    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductCategory.INVIERNO) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Formulario de Añadir Producto
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del producto") },
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

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory.name.capitalize(),
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
                        text = { Text(category.name.replace("_", " ").capitalize()) },
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
                    viewModel.addProduct(name.trim(), price, selectedCategory)
                    name = ""
                    priceText = ""
                    selectedCategory = ProductCategory.INVIERNO
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar producto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Productos ingresados (Inventario)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Se pasa la lista persistente: productList
        ProductList(products = productList, isClientView = false, onAddToCart = { })
    }
}