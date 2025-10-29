package Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.ProductViewModel

@Composable
fun ClientCartScreen(viewModel: ProductViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tu Carrito (${viewModel.cart.size} artículos)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (viewModel.cart.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.cart) { item ->
                    // Elemento del carrito: Nombre y precio
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.name, style = MaterialTheme.typography.bodyLarge)
                        Text("$${item.price}", style = MaterialTheme.typography.bodyLarge)
                    }
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Lógica de Compra / Pago */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceder al Pago")
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("El carrito está vacío. ¡Añade algo de ropa deportiva!")
            }
        }
    }
}