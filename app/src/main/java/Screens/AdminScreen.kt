package Screens // Asegúrate de que el paquete sea 'Screens'

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.ProductCategory // Importar el nuevo enum
import com.example.mitienda.ProductViewModel

 // Asumo que ProductList está en Screens, si no, ajusta el import

@Composable
fun AdminScreen(viewModel: ProductViewModel) {
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }

    // NUEVO: Estado para la categoría seleccionada
    var selectedCategory by remember { mutableStateOf(ProductCategory.INVIERNO) }
    var expanded by remember { mutableStateOf(false) } // Estado para el Dropdown

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

        // NUEVO: Dropdown para la Categoría
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory.name.capitalize(), // Muestra la categoría seleccionada
                onValueChange = {}, // No se puede cambiar escribiendo, solo seleccionando
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
                modifier = Modifier.fillMaxWidth(0.9f) // Ajustar el ancho
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
                    // CAMBIO CRUCIAL: Pasamos la categoría al addProduct
                    viewModel.addProduct(name.trim(), price, selectedCategory)
                    name = ""
                    priceText = ""
                    // Opcional: resetear categoría a INVIERNO
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

        // Lista de productos.
        ProductList(products = viewModel.products, isClientView = false, onAddToCart = { })
    }
}