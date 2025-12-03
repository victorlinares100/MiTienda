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
import com.example.mitienda.theme.BluePrimary // Importamos el azul

@Composable
fun ProductList(
    products: List<Product>,
    isClientView: Boolean,
    onAddToCart: (Product) -> Unit
) {
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No se encontraron productos", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp), // Menos espacio vertical
                    elevation = CardDefaults.cardElevation(2.dp), // Sombra más suave
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen
                        AsyncImage(
                            model = product.image?.url ?: "https://via.placeholder.com/150",
                            contentDescription = product.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0F0F0)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            // PRECIO EN AZUL
                            Text(
                                text = "$${product.price}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = BluePrimary,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = "${product.category?.nombre ?: "Sin Cat."} | ${product.brand?.nombre ?: "Genérico"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        // BOTÓN EN AZUL
                        if (isClientView) {
                            Button(
                                onClick = { onAddToCart(product) },
                                enabled = product.stock > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BluePrimary, // Azul
                                    disabledContainerColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("Añadir")
                            }
                        }
                    }
                }
            }
        }
    }
}