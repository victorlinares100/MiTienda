package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ViewModel.ProductViewModel
import Model.Product
import coil.compose.AsyncImage

@Composable
fun HomeScreen(
    viewModel: ProductViewModel,
    onGoToCatalog: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = uiState.productList

    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    if (selectedProduct != null) {
        ProductDetailScreen(
            product = selectedProduct!!,
            viewModel = viewModel,
            onBack = { selectedProduct = null }
        )
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // --- ENCABEZADO ---
                Text(
                    text = "¡Bienvenido a Mi Tienda!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lo mejor en ropa deportiva.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("Destacados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // --- GRILLA DE PRODUCTOS (El cambio principal) ---
                if (products.isNotEmpty()) {
                    // Tomamos los primeros 6 para mostrar en el Home
                    val featured = products.take(6)

                    // LazyVerticalGrid crea las columnas (2 columnas fijas)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 2 Columnas
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .weight(1f) // Esto hace que la grilla ocupe todo el espacio disponible al medio
                            .fillMaxWidth()
                    ) {
                        items(featured) { product ->
                            FeaturedProductCard(
                                product = product,
                                onDetail = { selectedProduct = product }
                            )
                        }
                    }
                } else {
                    // Estado vacío
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- BOTÓN INFERIOR ---
                Button(
                    onClick = { onGoToCatalog?.invoke() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Ver Catálogo Completo")
                }
            }
        }
    }
}

@Composable
private fun FeaturedProductCard(product: Product, onDetail: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp), // Altura fija para que todas las tarjetas sean iguales
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Le damos clic a toda la tarjeta
        onClick = onDetail
    ) {
        Column {
            // --- IMAGEN (Ahora sí se ve) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp) // La imagen ocupa la mitad superior
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = product.image?.url ?: "https://via.placeholder.com/150",
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Recorta la imagen para llenar el cuadro
                )
            }

            // --- TEXTOS ---
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.brand?.nombre ?: "Genérico",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}