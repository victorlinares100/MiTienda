package Screens

import Model.Product
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProductList(
    products: List<Product>,
    isClientView: Boolean,
    onAddToCart: (Product) -> Unit
) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // O Text("No hay productos") si no está cargando
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para que no lo tape la barra de abajo
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // --- IMAGEN DEL PRODUCTO ---
                        AsyncImage(
                            model = product.image?.url ?: "https://via.placeholder.com/150",
                            contentDescription = product.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // --- INFO DEL PRODUCTO ---
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "$${product.price}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Mostramos Categoría y Marca en pequeño
                            Text(
                                text = "${product.category?.nombre ?: "Sin Cat."} | ${product.brand?.nombre ?: "Genérico"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            // Mostramos Stock
                            Text(
                                text = if (product.stock > 0) "Stock: ${product.stock}" else "Agotado",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (product.stock > 0) Color.DarkGray else Color.Red
                            )
                        }

                        // --- BOTÓN AGREGAR ---
                        if (isClientView) {
                            Button(
                                onClick = { onAddToCart(product) },
                                enabled = product.stock > 0 // Solo si hay stock
                            ) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }
    }
}