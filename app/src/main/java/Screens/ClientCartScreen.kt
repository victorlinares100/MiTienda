package Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mitienda.Product
import com.example.mitienda.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun ClientCartScreen(viewModel: ProductViewModel) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔹 Estado local para manejar cantidades (map id → cantidad)
    val quantities = remember { mutableStateMapOf<Int, Int>() }

    // 🔹 Calcula el total general
    val total = viewModel.cart.sumOf { product ->
        val qty = quantities[product.id] ?: 1
        product.price * qty
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Tu Carrito (${viewModel.cart.size} artículos)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.cart.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(viewModel.cart) { product ->
                        val qty = quantities.getOrPut(product.id) { 1 }
                        val subtotal = product.price * qty

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium)
                                Text("Precio unitario: $${product.price}")
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // 🔹 Botones de cantidad
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = {
                                            if (qty > 1) {
                                                quantities[product.id] = qty - 1
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("La cantidad mínima es 1")
                                                }
                                            }
                                        }) {
                                            Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                                        }
                                        Text(qty.toString(), style = MaterialTheme.typography.bodyLarge)
                                        IconButton(onClick = {
                                            quantities[product.id] = qty + 1
                                        }) {
                                            Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                                        }
                                    }

                                    // 🔹 Subtotal
                                    Text("Subtotal: $${"%.2f".format(subtotal)}")

                                    // 🔹 Botón eliminar
                                    IconButton(onClick = {
                                        viewModel.removeFromCart(product)
                                        quantities.remove(product.id)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("${product.name} eliminado del carrito")
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar producto")
                                    }
                                }
                            }
                        }
                    }
                }

                // 🔹 Total general
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Total general: $${"%.2f".format(total)}",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Compra realizada con éxito 🎉")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Proceder al Pago")
                }
            } else {
                // 🔹 Estado vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("El carrito está vacío. ¡Añade algo de ropa deportiva!")
                }
            }
        }
    }
}
