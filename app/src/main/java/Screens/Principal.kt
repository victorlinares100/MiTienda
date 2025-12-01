package Screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ViewModel.ProductViewModel

// Definición de rutas internas del cliente
enum class ClientScreenRoute(val title: String) {
    HOME("Inicio"),
    CATALOG("Catálogo"),
    CART("Carrito"),
    NOS("Nosotros")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaApp(
    viewModel: ProductViewModel,
    onLogout: () -> Unit // <--- Nuevo parámetro para avisar al Main que cerramos sesión
) {
    // Ya no gestionamos roles aquí, asumimos que somos CLIENTE
    var clientScreen by remember { mutableStateOf(ClientScreenRoute.HOME) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = clientScreen.title)
                },
                actions = {
                    IconButton(onClick = {
                        // Llamamos a la función que nos pasó el MainActivity
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.HOME,
                    onClick = { clientScreen = ClientScreenRoute.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )

                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.CATALOG,
                    onClick = { clientScreen = ClientScreenRoute.CATALOG },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Catálogo") },
                    label = { Text("Catálogo") }
                )

                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.CART,
                    onClick = { clientScreen = ClientScreenRoute.CART },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
                    label = { Text("Carrito (${viewModel.cart.size})") } // Muestra contador
                )

                NavigationBarItem(
                    selected = clientScreen == ClientScreenRoute.NOS,
                    onClick = { clientScreen = ClientScreenRoute.NOS },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Nosotros") },
                    label = { Text("Nosotros") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            // Contenido solo del CLIENTE
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