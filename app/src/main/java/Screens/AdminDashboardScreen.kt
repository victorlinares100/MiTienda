package Screens

import ViewModel.ProductViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mitienda.theme.BlueDarkBackground
import com.example.mitienda.theme.BluePrimary
import com.example.mitienda.theme.ErrorRed // O el color rojo que tengas definido, si no usa Color.Red

enum class AdminScreenRoute(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PRODUCTS("Productos", Icons.Default.Inventory),
    CATEGORIES("Categorías", Icons.Default.Category)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ProductViewModel,
    onLogout: () -> Unit
) {
    // 1. OBTENEMOS LOS DATOS PARA CALCULAR ESTADÍSTICAS
    val uiState by viewModel.uiState.collectAsState()

    // Cálculos simples
    val totalProductos = uiState.productList.size
    val stockTotal = uiState.productList.sumOf { it.stock }
    val valorInventario = uiState.productList.sumOf { it.price * it.stock }
    val productosBajoStock = uiState.productList.count { it.stock < 5 } // Alerta si hay menos de 5

    var currentRoute by remember { mutableStateOf(AdminScreenRoute.DASHBOARD) } // Empezamos en Dashboard para ver los cambios
    val routes = AdminScreenRoute.values().toList()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Panel Administrativo", color = Color.White) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BlueDarkBackground,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Salir")
                        }
                    }
                )

                ScrollableTabRow(
                    selectedTabIndex = routes.indexOf(currentRoute),
                    containerColor = Color.White,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        if (routes.indexOf(currentRoute) < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[routes.indexOf(currentRoute)]),
                                color = BluePrimary
                            )
                        }
                    }
                ) {
                    routes.forEach { route ->
                        val isSelected = currentRoute == route
                        Tab(
                            selected = isSelected,
                            onClick = { currentRoute = route },
                            text = {
                                Text(
                                    text = route.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BluePrimary else Color.Gray
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = route.icon,
                                    contentDescription = route.title,
                                    tint = if (isSelected) BluePrimary else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            when (currentRoute) {
                AdminScreenRoute.DASHBOARD -> {
                    // --- AQUÍ ESTÁ EL DASHBOARD VISUAL ---
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                "Resumen de la Tienda",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BlueDarkBackground
                            )
                        }

                        item {
                            // Fila 1 de tarjetas
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatCard(
                                    title = "Productos",
                                    value = totalProductos.toString(),
                                    icon = Icons.Default.Inventory,
                                    color = BluePrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Stock Total",
                                    value = stockTotal.toString(),
                                    icon = Icons.Default.ShowChart, // Icono de gráfico
                                    color = Color(0xFF2E7D32), // Verde
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        item {
                            // Fila 2 de tarjetas
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatCard(
                                    title = "Valor Inventario",
                                    value = "$${valorInventario}",
                                    icon = Icons.Default.AttachMoney,
                                    color = Color(0xFFF57C00), // Naranja
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Stock Bajo (<5)",
                                    value = productosBajoStock.toString(),
                                    icon = Icons.Default.Warning,
                                    color = if(productosBajoStock > 0) Color.Red else Color.Gray, // Rojo si hay alerta
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                AdminScreenRoute.PRODUCTS -> {
                    AdminProductScreen(viewModel)
                }
                AdminScreenRoute.CATEGORIES -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Próximamente: Categorías", color = Color.Gray)
                    }
                }
            }
        }
    }
}

// --- COMPONENTE AUXILIAR PARA LAS TARJETAS ---
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = BlueDarkBackground
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}