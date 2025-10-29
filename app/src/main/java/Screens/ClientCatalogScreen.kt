package Screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.ProductCategory
import com.example.mitienda.ProductViewModel
import com.example.mitienda.Product
import androidx.compose.runtime.collectAsState // Importante para StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCatalogScreen(viewModel: ProductViewModel) {

    // CAMBIO CRUCIAL: Observar el StateFlow de la base de datos
    val uiState by viewModel.uiState.collectAsState()
    val allProducts = uiState.productList // La lista persistente

    var selectedCategory: ProductCategory? by remember { mutableStateOf(null) }

    // Lista de productos a mostrar (filtrada)
    // CAMBIO: Usa la lista persistente (allProducts) como dependencia
    val filteredProducts: List<Product> = remember(allProducts, selectedCategory) {
        if (selectedCategory == null) {
            allProducts // Muestra la lista completa de la DB
        } else {
            // Filtra la lista de la DB
            allProducts.filter { it.category == selectedCategory }
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