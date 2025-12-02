package com.example.mitienda

import Data.ProductRepository
import Data.CarritoRepository
import Data.UserRepository
import Model.Rol
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme // Importamos el tema estándar
import androidx.compose.runtime.*
import Screens.LoginScreen
import Screens.RegisterScreen
import Screens.AdminScreen
import Screens.TiendaApp
import ViewModel.ProductViewModel
import ViewModel.ViewModelFactory
import com.example.mitienda.theme.MiTiendaTheme // Importamos tu tema personalizado

class MainActivity : ComponentActivity() {

    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory(
            ProductRepository(),
            CarritoRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("login") }

            when (currentScreen) {
                // CASO 1: LOGIN (Usamos tu diseño bonito)
                "login" -> {
                    MiTiendaTheme { // <--- APLICAMOS EL TEMA AZUL AQUÍ
                        LoginScreen(
                            onLoginSuccess = { rol ->
                                if (rol == Rol.ADMIN) {
                                    currentScreen = "admin"
                                } else {
                                    currentScreen = "cliente"
                                }
                            },
                            onNavigateToRegister = {
                                currentScreen = "registro"
                            }
                        )
                    }
                }

                // CASO 2: REGISTRO (Usamos tu diseño bonito)
                "registro" -> {
                    MiTiendaTheme { // <--- APLICAMOS EL TEMA AZUL AQUÍ
                        RegisterScreen(
                            onRegisterSuccess = {
                                currentScreen = "login"
                            },
                            onNavigateToLogin = {
                                currentScreen = "login"
                            }
                        )
                    }
                }

                // CASO 3: ADMIN (Usamos el tema estándar de Android por ahora)
                "admin" -> {
                    MaterialTheme { // <--- TEMA POR DEFECTO (Blanco/Limpio)
                        AdminScreen(
                            viewModel = productViewModel,
                            onLogout = {
                                Data.UserRepository.logout()
                                currentScreen = "login"
                            }
                        )
                    }
                }

                // CASO 4: CLIENTE (Usamos el tema estándar de Android por ahora)
                "cliente" -> {
                    MaterialTheme { // <--- TEMA POR DEFECTO (Blanco/Limpio)
                        TiendaApp(
                            viewModel = productViewModel,
                            onLogout = {
                                UserRepository.logout()
                                currentScreen = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}