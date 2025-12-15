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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mitienda.theme.BlueDarkBackground
import com.example.mitienda.theme.BluePrimary
import java.text.NumberFormat
import java.util.Locale

// Definimos las rutas del menú
enum class AdminScreenRoute(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PRODUCTS("Productos", Icons.Default.Inventory),
    CATEGORIES("Categorías", Icons.Default.Category),
    USERS("Usuarios", Icons.Default.Group),
    STOCK("Alertas", Icons.Default.Warning)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ProductViewModel,
    onLogout: () -> Unit
) {
    // 1. OBTENEMOS LOS DATOS
    val uiState by viewModel.uiState.collectAsState()

    // --- CÁLCULOS MATEMÁTICOS ---
    val totalProductos = uiState.productList.size
    val stockTotal = uiState.productList.sumOf { it.stock }
    val valorInventario = uiState.productList.sumOf { it.price * it.stock }
    val productosBajoStock = uiState.productList.count { it.stock <= 5 }

    // Formateador de dinero (Ej: $ 1.000.000)
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    var currentRoute by remember { mutableStateOf(AdminScreenRoute.DASHBOARD) }
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

                // Barra de Pestañas
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
                    // --- DASHBOARD PRINCIPAL ---
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                "Resumen General",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = BlueDarkBackground
                            )
                        }

                        // --- AQUÍ ESTÁ EL CAMBIO: CUADRÍCULA 2x2 ---

                        // FILA 1: Productos y Unidades
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatCard(
                                    title = "Productos",
                                    value = totalProductos.toString(),
                                    icon = Icons.Default.Inventory,
                                    color = BluePrimary,
                                    modifier = Modifier.weight(1f) // Ocupa la mitad exacta
                                )
                                StatCard(
                                    title = "Unidades Totales",
                                    value = stockTotal.toString(),
                                    icon = Icons.Default.ShowChart,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.weight(1f) // Ocupa la mitad exacta
                                )
                            }
                        }

                        // FILA 2: Valor Inventario y Alertas
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatCard(
                                    title = "Valor Inventario",
                                    value = currencyFormat.format(valorInventario),
                                    icon = Icons.Default.AttachMoney,
                                    color = Color(0xFFF57C00),
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Alertas Stock",
                                    value = productosBajoStock.toString(),
                                    icon = Icons.Default.Warning,
                                    color = if (productosBajoStock > 0) Color.Red else Color.Gray,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                AdminScreenRoute.PRODUCTS -> AdminProductScreen(viewModel)
                AdminScreenRoute.CATEGORIES -> AdminCategoryScreen(viewModel)
                AdminScreenRoute.USERS -> AdminUserScreen(viewModel)
                AdminScreenRoute.STOCK -> AdminStockScreen(viewModel)
            }
        }
    }
}

// Tarjeta Estilo Flexible (Se adapta al ancho disponible)
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(130.dp), // Altura fija, ancho flexible
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Column {
                // Ajustamos el texto para que si es muy largo baje de línea, pero se vea completo
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BlueDarkBackground,
                    lineHeight = 24.sp,
                    maxLines = 2, // Permite 2 líneas si el precio es gigante
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}