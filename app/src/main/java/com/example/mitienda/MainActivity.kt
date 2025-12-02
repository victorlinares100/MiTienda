package com.example.mitienda

import Data.ProductRepository
import Data.CarritoRepository // <--- ¡Nuevo Import!
import Data.UserRepository
import Model.Rol
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import Screens.LoginScreen
import Screens.RegisterScreen
import Screens.AdminScreen
import Screens.TiendaApp
import ViewModel.ProductViewModel
import ViewModel.ViewModelFactory

class MainActivity : ComponentActivity() {

    // --- CAMBIO IMPORTANTE AQUÍ ---
    // Ahora pasamos AMBOS repositorios a la fábrica
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory(
            ProductRepository(),
            CarritoRepository() // <--- Agregamos esta línea
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Variable de estado para controlar la navegación
            var currentScreen by remember { mutableStateOf("login") }

            when (currentScreen) {
                "login" -> {
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

                "registro" -> {
                    RegisterScreen(
                        onRegisterSuccess = {
                            currentScreen = "login"
                        },
                        onNavigateToLogin = {
                            currentScreen = "login"
                        }
                    )
                }

                "admin" -> {
                    AdminScreen(
                        viewModel = productViewModel,
                        onLogout = {
                            Data.UserRepository.logout()
                            currentScreen = "login"
                        }
                    )
                }

                "cliente" -> {
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