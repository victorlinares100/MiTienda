package Screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import Model.ProductCategory
import ViewModel.ProductViewModel
import Model.Product
import androidx.compose.runtime.collectAsState // Importante para StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCatalogScreen(viewModel: ProductViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val allProducts = uiState.productList

    var selectedCategory: ProductCategory? by remember { mutableStateOf(null) }

    val filteredProducts: List<Product> = remember(allProducts, selectedCategory) {
        if (selectedCategory == null) {
            allProducts // Muestra la lista completa de la DB
        } else {
            allProducts.filter { it.category == selectedCategory }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // BARRA DE FILTROS DE CATEGORÍA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("Todos") }
            )

            ProductCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.name.capitalize()) }
                )
            }
        }

        Divider()

        ProductList(
            products = filteredProducts,
            isClientView = true,
            onAddToCart = { product -> viewModel.addToCart(product) }
        )
    }
}