package Screens // NOTA: Asumo que el paquete principal sigue siendo 'com.example.mitienda'

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mitienda.ProductViewModel
import com.example.mitienda.Rol

// Rutas de navegación interna para el cliente
// Lo definimos aquí porque pertenece al manejo de la navegación de TiendaApp
enum class ClientScreenRoute { CATALOG, CART }

// ==============================================================================
// 1. APP PRINCIPAL: MANEJO DE SESIÓN Y ROLES
// ==============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaApp(viewModel: ProductViewModel) {
    var currentRole by remember { mutableStateOf<Rol?>(null) }
    var clientScreen by remember { mutableStateOf(ClientScreenRoute.CATALOG) }

    // Si no hay rol, forzamos la pantalla de Login (asumo que LoginScreen está importado o en el mismo paquete)
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
                                Rol.CLIENT -> "Tienda - Catálogo"
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
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentRole) {
                    Rol.ADMIN -> AdminScreen(viewModel)
                    Rol.CLIENT -> {
                        when (clientScreen) {
                            ClientScreenRoute.CATALOG -> ClientCatalogScreen(viewModel)
                            ClientScreenRoute.CART -> ClientCartScreen(viewModel)
                        }
                    }
                    null -> { /* Nunca debería ocurrir aquí */ }
                }
            }
        }
    }
}