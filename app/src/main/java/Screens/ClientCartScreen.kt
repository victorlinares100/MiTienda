package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ViewModel.ProductViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun ClientCartScreen(viewModel: ProductViewModel) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. OBSERVAMOS EL ESTADO (Para saber si está cargando o hubo error)
    val uiState by viewModel.uiState.collectAsState()

    // Mapa de cantidades <ID_Producto, Cantidad>
    val quantities = remember { mutableStateMapOf<Long, Int>() }

    // Calculamos el total dinámicamente
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.cart.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(viewModel.cart) { product ->
                        val qty = quantities.getOrPut(product.id) { 1 }
                        val subtotal = product.price * qty

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // FOTO
                                AsyncImage(
                                    model = product.image?.url ?: "https://via.placeholder.com/150",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // DATOS
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Unitario: $${product.price}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Botones de cantidad
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    if (qty > 1) {
                                                        quantities[product.id] = qty - 1
                                                    } else {
                                                        scope.launch { snackbarHostState.showSnackbar("Mínimo 1 unidad") }
                                                    }
                                                },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = "Menos")
                                            }

                                            Text(
                                                qty.toString(),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            IconButton(
                                                onClick = { quantities[product.id] = qty + 1 },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "Más")
                                            }
                                        }

                                        // Subtotal y Borrar
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("$${"%.2f".format(subtotal)}", fontWeight = FontWeight.Bold)

                                            IconButton(onClick = {
                                                viewModel.removeFromCart(product)
                                                quantities.remove(product.id)
                                                scope.launch { snackbarHostState.showSnackbar("Eliminado") }
                                            }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "$${"%.2f".format(total)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. MOSTRAR ERRORES SI OCURREN
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 3. BOTÓN DE PAGO CONECTADO
                Button(
                    onClick = {
                        // AQUÍ LLAMAMOS A LA API REAL
                        viewModel.performCheckout(quantities) {
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Compra realizada con éxito! 🎉")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !uiState.isLoading // Deshabilitar si está cargando
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Procesando...")
                    } else {
                        Text("Pagar Ahora")
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío 🛒", color = Color.Gray)
                }
            }
        }
    }
}