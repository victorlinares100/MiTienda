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
import Model.Rol

enum class ClientScreenRoute(val title: String) {
    HOME("Inicio"),
    CATALOG("Catálogo"),
    CART("Carrito"),
    NOS("Nosotros")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaApp(viewModel: ProductViewModel) {
    var currentRole by remember { mutableStateOf<Rol?>(null) }
    var clientScreen by remember { mutableStateOf(ClientScreenRoute.HOME) }

    if (currentRole == null) {
        LoginScreen(
            onLoginSuccess = { rol ->
                currentRole = rol
            }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (currentRole) {
                                Rol.ADMIN -> "Tienda - Panel de Inventario"
                                // <-- CAMBIO: Título dinámico para el cliente
                                Rol.CLIENT -> clientScreen.title
                                else -> "Tienda"
                            }
                        )
                    },
                    actions = {
                        IconButton(onClick = { currentRole = null }) {
                            Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                        }
                    }
                )
            },
            bottomBar = {
                if (currentRole == Rol.CLIENT) {
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
                            label = { Text("Carrito (${viewModel.cart.size})") }
                        )

                        NavigationBarItem(
                            selected = clientScreen == ClientScreenRoute.NOS,
                            onClick = { clientScreen = ClientScreenRoute.NOS },
                            icon = { Icon(Icons.Default.Info, contentDescription = "Nosotros") },
                            label = { Text("Nosotros") }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentRole) {
                    Rol.ADMIN -> AdminScreen(viewModel) //
                    Rol.CLIENT -> {
                        when (clientScreen) {
                            ClientScreenRoute.HOME -> HomeScreen(viewModel = viewModel, onGoToCatalog = { clientScreen = ClientScreenRoute.CATALOG })
                            ClientScreenRoute.CATALOG -> ClientCatalogScreen(viewModel)
                            ClientScreenRoute.CART -> ClientCartScreen(viewModel)
                            ClientScreenRoute.NOS -> NosotrosScreen()
                        }
                    }
                    null -> {}
                }
            }
        }
    }
}
