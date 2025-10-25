package Screens // Asegúrate de que el paquete sea 'Screens'

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.ProductCategory // Importar el enum de Categorías
import com.example.mitienda.ProductViewModel
import com.example.mitienda.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCatalogScreen(viewModel: ProductViewModel) {

    // Estado para la categoría seleccionada (null = "Todos")
    var selectedCategory: ProductCategory? by remember { mutableStateOf(null) }

    // Lista de productos a mostrar (filtrada)
    val filteredProducts: List<Product> = remember(viewModel.products, selectedCategory) {
        if (selectedCategory == null) {
            viewModel.products
        } else {
            // Utilizamos la función de filtrado directamente en la lista de productos
            viewModel.products.filter { it.category == selectedCategory }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // 1. BARRA DE FILTROS DE CATEGORÍA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip "TODOS"
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("Todos") }
            )

            // Chips para cada categoría definida
            ProductCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.name.capitalize()) }
                )
            }
        }

        Divider()

        // 2. LISTA DE PRODUCTOS FILTRADOS
        ProductList(
            products = filteredProducts, // Usar la lista filtrada
            isClientView = true,
            onAddToCart = { product -> viewModel.addToCart(product) }
        )
    }
}