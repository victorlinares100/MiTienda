package Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mitienda.ProductViewModel
import com.example.mitienda.Product
import com.example.mitienda.ProductCategory
import com.example.mitienda.R

@Composable
fun HomeScreen(
    viewModel: ProductViewModel,
    onGoToCatalog: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = uiState.productList

    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    if (selectedProduct != null) {
        // Mostrar la pantalla de detalles
        ProductDetailScreen(
            product = selectedProduct!!,
            viewModel = viewModel,
            onBack = { selectedProduct = null }
        )
    } else {
        // Pantalla principal
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "¡Bienvenido a Mi Tienda!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ropa deportiva y de temporada seleccionada para ti.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))

                Text("Destacados", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (products.isNotEmpty()) {
                    val featured = products.take(6)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        items(featured) { product ->
                            FeaturedProductCard(
                                product = product,
                                onDetail = { selectedProduct = product }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay productos cargados aún. Visita el catálogo.")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { onGoToCatalog?.invoke() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Ver Catálogo")
                }
            }
        }
    }
}


@Composable
private fun FeaturedProductCard(product: Product, onDetail: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Precio: $${"%.2f".format(product.price)}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onDetail, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Detalle")
            }
        }
    }
}

