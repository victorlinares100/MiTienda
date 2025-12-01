package Screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ViewModel.ProductViewModel
import Model.Product
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCatalogScreen(viewModel: ProductViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val allProducts = uiState.productList
    val categorias = uiState.categorias // Obtenemos las categorías de la API

    // Ahora filtramos por ID (Long) en vez de Enum
    var selectedCategoryId: Long? by remember { mutableStateOf(null) }

    // Lógica de filtrado
    val filteredProducts: List<Product> = remember(allProducts, selectedCategoryId) {
        if (selectedCategoryId == null) {
            allProducts // Mostrar todos
        } else {
            // Filtramos si el ID de la categoría del producto coincide con el seleccionado
            allProducts.filter { it.category?.id == selectedCategoryId }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // --- BARRA DE FILTROS DE CATEGORÍA ---
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip "Todos"
            FilterChip(
                selected = selectedCategoryId == null,
                onClick = { selectedCategoryId = null },
                label = { Text("Todos") }
            )

            // Chips dinámicos desde la API
            categorias.forEach { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { selectedCategoryId = category.id },
                    label = { Text(category.nombre) }
                )
            }
        }

        Divider()

        // Reutilizamos la lista que ya arreglamos antes
        ProductList(
            products = filteredProducts,
            isClientView = true,
            onAddToCart = { product -> viewModel.addToCart(product) }
        )
    }
}