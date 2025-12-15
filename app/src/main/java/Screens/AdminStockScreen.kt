package Screens

import ViewModel.ProductViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mitienda.theme.ErrorRed // O usa Color.Red si no tienes este color

@Composable
fun AdminStockScreen(viewModel: ProductViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    // FILTRO MÁGICO: Solo productos con 5 unidades o menos
    val lowStockProducts = uiState.productList.filter { it.stock <= 5 }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Alertas de Inventario",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Productos con menos de 5 unidades",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (lowStockProducts.isEmpty()) {
            // Caso feliz: Todo tiene stock
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("¡Todo en orden! No hay stock crítico.", color = Color.Gray)
            }
        } else {
            // Lista de alertas
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lowStockProducts) { product ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), // Fondo rojizo suave
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.Bold)
                                Text("Quedan solo: ${product.stock}", color = Color.Red, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { /* Podrías navegar a editar */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("Ver")
                            }
                        }
                    }
                }
            }
        }
    }
}