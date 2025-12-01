package Screens

import Model.Product
import ViewModel.ProductViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Scroll por si la pantalla es chica
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- IMAGEN GRANDE ---
            AsyncImage(
                model = product.image?.url ?: "https://via.placeholder.com/300",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- TÍTULO Y PRECIO ---
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- TARJETA DE DETALLES ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Categoría", product.category?.nombre ?: "N/A")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Marca", product.brand?.nombre ?: "N/A")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Talla", product.size?.nombre ?: "N/A")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow("Stock Disponible", product.stock.toString())
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTONES ---
            Button(
                onClick = {
                    viewModel.addToCart(product)
                    onBack() // Volvemos al catálogo tras agregar
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = product.stock > 0
            ) {
                Text(if (product.stock > 0) "Agregar al Carrito" else "Agotado")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onBack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Catálogo")
            }
        }
    }
}

// Helper pequeño para las filas de detalles
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}