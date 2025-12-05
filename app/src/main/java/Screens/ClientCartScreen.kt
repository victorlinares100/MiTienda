package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ViewModel.ProductViewModel
import Model.Product
import androidx.compose.material.icons.filled.ShoppingCart
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.mitienda.theme.*
import com.google.android.engage.shopping.datamodel.ShoppingCart


@Composable
fun ClientCartScreen(viewModel: ProductViewModel) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()

    // Estado local para cantidades
    val quantities = remember { mutableStateMapOf<Long, Int>() }

    // Calcular Total
    val total = viewModel.cart.sumOf { product ->
        val qty = quantities[product.id] ?: 1
        product.price * qty
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA) // Fondo General
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- TÍTULO ---
            Text(
                text = "Tu Carrito (${viewModel.cart.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = BlueDarkBackground,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 16.dp)
            )

            if (viewModel.cart.isNotEmpty()) {
                // --- LISTA DE PRODUCTOS ---
                LazyColumn(
                    modifier = Modifier
                        .weight(1f) // Ocupa todo el espacio disponible
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(viewModel.cart) { product ->
                        val qty = quantities.getOrPut(product.id) { 1 }
                        CartItemCard(
                            product = product,
                            quantity = qty,
                            onIncrease = { quantities[product.id] = qty + 1 },
                            onDecrease = {
                                if (qty > 1) quantities[product.id] = qty - 1
                                else scope.launch { snackbarHostState.showSnackbar("Mínimo 1 unidad") }
                            },
                            onRemove = {
                                viewModel.removeFromCart(product)
                                quantities.remove(product.id)
                            }
                        )
                    }
                }

                // --- ZONA DE PAGO (Fixed Bottom Sheet style) ---
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Color.White,
                    shadowElevation = 16.dp // Sombra fuerte hacia arriba
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        // Fila de Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total a Pagar",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextGray
                            )
                            Text(
                                text = "$${total.toInt()}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = BluePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Mensaje de Error (si hay)
                        if (uiState.errorMessage != null) {
                            Text(
                                text = uiState.errorMessage ?: "",
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Botón de Pago
                        Button(
                            onClick = {
                                viewModel.performCheckout(quantities) {
                                    scope.launch { snackbarHostState.showSnackbar("¡Compra realizada con éxito!") }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BluePrimary,
                                disabledContainerColor = BluePrimary.copy(alpha = 0.5f)
                            ),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Pagar Ahora", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

            } else {
                // --- ESTADO VACÍO ---
                EmptyCartState()
            }
        }
    }
}

// --- COMPONENTE: TARJETA DE ITEM DE CARRITO ---
@Composable
fun CartItemCard(
    product: Product,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            AsyncImage(
                model = product.image?.url ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Info y Controles
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Nombre y Borrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BlueDarkBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Botón Borrar discreto
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remove",
                        tint = ErrorRed.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onRemove() }
                    )
                }

                // Bottom: Precio y Controles de Cantidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )

                    // Controles de Cantidad (Estilo Cápsula)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFF5F7FA), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        QuantityButton(icon = Icons.Default.Remove, onClick = onDecrease)
                        Text(
                            text = quantity.toString(),
                            modifier = Modifier.padding(horizontal = 12.dp),
                            fontWeight = FontWeight.Bold,
                            color = BlueDarkBackground
                        )
                        QuantityButton(icon = Icons.Default.Add, onClick = onIncrease)
                    }
                }
            }
        }
    }
}

// Botón + / - pequeño
@Composable
fun QuantityButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .clickable { onClick() }
            .border(1.dp, InputBorder, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = TextGray
        )
    }
}

// Estado Vacío
@Composable
fun EmptyCartState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart, // Si te da error aquí, usa Icons.Filled.ShoppingCart
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = TextGray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.titleLarge,
            color = TextGray
        )
    }
}