package com.example.mitienda

import Data.ProductRepository
import Data.CarritoRepository
import Data.UserRepository
import Model.Rol
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
// CAMBIO 1: Importamos la nueva pantalla Dashboard
import Screens.AdminDashboardScreen
import Screens.LoginScreen
import Screens.RegisterScreen
import Screens.TiendaApp
import ViewModel.ProductViewModel
import ViewModel.ViewModelFactory
import com.example.mitienda.theme.MiTiendaTheme

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

                "login" -> {
                    MiTiendaTheme {
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


                "registro" -> {
                    MiTiendaTheme {
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

                // CAMBIO 2: Aquí llamamos al Dashboard en lugar de AdminScreen
                "admin" -> {
                    // Recomiendo usar MiTiendaTheme aquí también para que los colores se vean bien
                    MiTiendaTheme {
                        AdminDashboardScreen(
                            viewModel = productViewModel,
                            onLogout = {
                                Data.UserRepository.logout()
                                currentScreen = "login"
                            }
                        )
                    }
                }


                "cliente" -> {
                    MiTiendaTheme {
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