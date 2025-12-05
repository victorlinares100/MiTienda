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
import Screens.LoginScreen
import Screens.RegisterScreen
import Screens.AdminScreen
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


                "admin" -> {
                    MaterialTheme {
                        AdminScreen(
                            viewModel = productViewModel,
                            onLogout = {
                                Data.UserRepository.logout()
                                currentScreen = "login"
                            }
                        )
                    }
                }


                "cliente" -> {
                    MaterialTheme {
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