package Screens

// 1. OJO: Solo usamos imports de Material 3 y Runtime.
// Si ves "androidx.compose.material.*" (sin el 3) BORRALO, excepto para los Iconos.
import ViewModel.ProductViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.* // Importante: Material 3
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // Para la animación de la pestaña
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mitienda.theme.BlueDarkBackground
import com.example.mitienda.theme.BluePrimary

enum class AdminScreenRoute(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
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
    var currentRoute by remember { mutableStateOf(AdminScreenRoute.PRODUCTS) }
    val routes = AdminScreenRoute.values().toList()

    Scaffold(
        topBar = {
            Column {
                // Usamos CenterAlignedTopAppBar que es más estable en algunas versiones
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
        // Contenido principal
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            when (currentRoute) {
                AdminScreenRoute.DASHBOARD -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("Próximamente: Estadísticas", color = Color.Gray)
                    }
                }
                AdminScreenRoute.PRODUCTS -> {
                    // Aquí llamamos a tu pantalla de productos
                    AdminProductScreen(viewModel)
                }
                AdminScreenRoute.CATEGORIES -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("Próximamente: Categorías", color = Color.Gray)
                    }
                }
            }
        }
    }
}