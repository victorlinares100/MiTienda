package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ViewModel.ProductViewModel
import com.example.mitienda.theme.*

enum class ClientScreenRoute(val title: String, val subtitle: String) {
    HOME("Hola, bienvenido!", "Encuentra tu estilo"),
    CATALOG("Catálogo", "Explora nuestra colección"),
    CART("Tu Carrito", "Finaliza tu compra"),
    NOS("Nosotros", "Conoce nuestra historia")
}

@Composable
fun TiendaApp(
    viewModel: ProductViewModel,
    onLogout: () -> Unit
) {
    var clientScreen by remember { mutableStateOf(ClientScreenRoute.HOME) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(BlueDarkBackground, BlueLightBackground)
                        )
                    )
                    .shadow(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 24.dp, bottom = 24.dp)
                ) {
                    Text(
                        text = clientScreen.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = clientScreen.subtitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Salir",
                        tint = Color.White
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.HOME,
                    onClick = { clientScreen = ClientScreenRoute.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BlueLightBackground.copy(alpha = 0.2f))
                )
                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.CATALOG,
                    onClick = { clientScreen = ClientScreenRoute.CATALOG },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Catálogo") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BlueLightBackground.copy(alpha = 0.2f))
                )
                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.CART,
                    onClick = { clientScreen = ClientScreenRoute.CART },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Carrito (${viewModel.cart.size})") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BlueLightBackground.copy(alpha = 0.2f))
                )
                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.NOS,
                    onClick = { clientScreen = ClientScreenRoute.NOS },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Nosotros") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BlueLightBackground.copy(alpha = 0.2f))
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            when (clientScreen) {
                ClientScreenRoute.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onGoToCatalog = { clientScreen = ClientScreenRoute.CATALOG }
                )
                ClientScreenRoute.CATALOG -> ClientCatalogScreen(viewModel)
                ClientScreenRoute.CART -> ClientCartScreen(viewModel)
                ClientScreenRoute.NOS -> NosotrosScreen()
            }
        }
    }
}