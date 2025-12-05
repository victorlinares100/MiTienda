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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mitienda.theme.*

@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: ProductViewModel,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F7FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AsyncImage(
                model = product.image?.url ?: "https://via.placeholder.com/300",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) // Curva solo abajo
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = BlueDarkBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$${product.price.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = BluePrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Especificaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = BlueDarkBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DetailRow("Categoría", product.category?.nombre ?: "N/A")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = InputBorder)
                        DetailRow("Marca", product.brand?.nombre ?: "N/A")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = InputBorder)
                        DetailRow("Talla", product.size?.nombre ?: "N/A")
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = InputBorder)
                        DetailRow("Stock Disponible", product.stock.toString(), isStock = true, stock = product.stock)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.addToCart(product)
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    enabled = product.stock > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        if (product.stock > 0) "Agregar al Carrito" else "Agotado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { onBack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp), // Botón redondeado
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(InputBorder))
                ) {
                    Text("Volver al Catálogo", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isStock: Boolean = false, stock: Int = 0) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = BlueDarkBackground)
        Text(
            text = value,
            fontWeight = if (isStock) FontWeight.Bold else FontWeight.Normal,
            color = if (isStock) {
                if (stock > 5) SuccessGreen
                else if (stock > 0) WarningOrange
                else ErrorRed
            } else {
                TextGray
            }
        )
    }
}