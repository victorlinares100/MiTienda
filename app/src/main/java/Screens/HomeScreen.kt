package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ViewModel.ProductViewModel
import Model.Product
import coil.compose.AsyncImage
import com.example.mitienda.theme.*

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
        // Ya no necesitamos Surface ni el Header Box aquí, porque vienen de TiendaApp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                // TRUCO VISUAL: Subimos el contenido (-30dp) para que se superponga al header de TiendaApp
                .offset(y = (-30).dp)
        ) {

            // Subtítulo de sección (Opcional, o puedes quitarlo)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(50),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Destacados de temporada",
                    style = MaterialTheme.typography.labelLarge,
                    color = BlueDarkBackground,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // --- GRILLA ---
            if (products.isNotEmpty()) {
                val featured = products.take(6)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(featured) { product ->
                        FeaturedProductCardModern(
                            product = product,
                            onDetail = { selectedProduct = product },
                            onQuickAdd = { viewModel.addToCart(product) }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- BOTÓN GRANDE ---
            Button(
                onClick = { onGoToCatalog?.invoke() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    text = "Ver Catálogo Completo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ... La función FeaturedProductCardModern SE QUEDA IGUAL, no hace falta cambiarla ...
@Composable
private fun FeaturedProductCardModern(
    product: Product,
    onDetail: () -> Unit,
    onQuickAdd: () -> Unit
) {
    // ... (Pega aquí el mismo código de FeaturedProductCardModern que te pasé en la respuesta anterior) ...
    // Te lo resumo aquí para que el archivo esté completo:
    Card(
        modifier = Modifier.fillMaxWidth().height(240.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = onDetail
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(130.dp).background(Color(0xFFF0F0F0))) {
                AsyncImage(model = product.image?.url, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Surface(Modifier.align(Alignment.TopStart).padding(8.dp), shape = RoundedCornerShape(8.dp), color = BlueDarkBackground.copy(alpha = 0.8f)) {
                    Text(product.category?.nombre ?: "Varios", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Column(Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(product.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Black)
                    Text(product.brand?.nombre ?: "Genérico", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("$${product.price.toInt()}", style = MaterialTheme.typography.titleMedium, color = BluePrimary, fontWeight = FontWeight.ExtraBold)
                    IconButton(onClick = onQuickAdd, modifier = Modifier.size(32.dp).clip(CircleShape).background(BlueLightBackground.copy(alpha = 0.1f))) {
                        Icon(Icons.Default.AddShoppingCart, "Add", tint = BluePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}