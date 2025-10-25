package Screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.Product

@Composable
fun ProductList(products: List<Product>, isClientView: Boolean, onAddToCart: (Product) -> Unit) {
    if (products.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay productos aún")
        }
    } else {
        LazyColumn {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text("$${product.price}", style = MaterialTheme.typography.bodyMedium)
                            // TODO: Mostrar la categoría aquí
                        }
                        // El botón "Agregar" solo se muestra en la vista del cliente
                        if (isClientView) {
                            Button(onClick = { onAddToCart(product) }) {
                                Text("Agregar")
                            }
                        }
                    }
                }
            }
        }
    }
}