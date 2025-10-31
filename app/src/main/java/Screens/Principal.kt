package Screens // NOTA: Asumo que el paquete principal sigue siendo 'com.example.mitienda'

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
// <-- CAMBIO: Importamos los iconos nuevos
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mitienda.ProductViewModel
import com.example.mitienda.Rol

// <-- CAMBIO: Añadimos HOME y ABOUT_US al enum.
// También añadí una propiedad 'title' para que el título de la TopBar sea dinámico.
enum class ClientScreenRoute(val title: String) {
    HOME("Inicio"),
    CATALOG("Catálogo"),
    CART("Carrito"),
    NOS("Nosotros")
}

// ==============================================================================
// 1. APP PRINCIPAL: MANEJO DE SESIÓN Y ROLES
// ==============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaApp(viewModel: ProductViewModel) {
    var currentRole by remember { mutableStateOf<Rol?>(null) }
    // <-- CAMBIO: La pantalla inicial ahora es HOME
    var clientScreen by remember { mutableStateOf(ClientScreenRoute.HOME) }

    // Si no hay rol, forzamos la pantalla de Login
    if (currentRole == null) {
        LoginScreen(
            onLoginSuccess = { rol ->
                currentRole = rol // Establece el rol y pasa a la vista principal
            }
        )
    } else {
        // Usuario logueado: Mostramos la interfaz de la aplicación
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
                    // Botón de Cerrar Sesión
                    actions = {
                        IconButton(onClick = { currentRole = null }) {
                            Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión")
                        }
                    }
                )
            },
            // Barra de Navegación Inferior (solo para CLIENTE)
            bottomBar = {
                if (currentRole == Rol.CLIENT) {
                    NavigationBar {
                        // <-- CAMBIO: Añadimos el item "Home"
                        NavigationBarItem(
                            selected = clientScreen == ClientScreenRoute.HOME,
                            onClick = { clientScreen = ClientScreenRoute.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                            label = { Text("Inicio") }
                        )

                        // Item "Catálogo" (existente)
                        NavigationBarItem(
                            selected = clientScreen == ClientScreenRoute.CATALOG,
                            onClick = { clientScreen = ClientScreenRoute.CATALOG },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Catálogo") },
                            label = { Text("Catálogo") }
                        )

                        // Item "Carrito" (existente)
                        NavigationBarItem(
                            selected = clientScreen == ClientScreenRoute.CART,
                            onClick = { clientScreen = ClientScreenRoute.CART },
                            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
                            label = { Text("Carrito (${viewModel.cart.size})") }
                        )

                        // <-- CAMBIO: Añadimos el item "Nosotros"
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
                    Rol.ADMIN -> AdminScreen(viewModel) // <-- Asumo que esta pantalla existe
                    Rol.CLIENT -> {
                        // <-- CAMBIO: Añadimos las nuevas pantallas al 'when'
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
