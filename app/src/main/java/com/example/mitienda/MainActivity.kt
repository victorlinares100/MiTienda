package com.example.mitienda

import Data.AppDatabase
import Data.ProductRepository
import Data.UserRepository // Importamos el Repo para limpiar la sesión al salir
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

    // Inicializa el ViewModel de Productos
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory(
            ProductRepository(
                AppDatabase.getDatabase(this).productoDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Variable de estado para controlar la navegación básica
            var currentScreen by remember { mutableStateOf("login") }

            when (currentScreen) {
                "login" -> {
                    LoginScreen(
                        onLoginSuccess = { rol ->
                            // Redirigir según el rol recibido desde la API
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
                            // Al terminar registro, vamos al login
                            currentScreen = "login"
                        },
                        onNavigateToLogin = {
                            // Si cancela, vuelve al login
                            currentScreen = "login"
                        }
                    )
                }

                "admin" -> {
                    // Pantalla de Administración
                    // Aquí podrías agregar un botón de logout en el futuro si AdminScreen lo soporta
                    AdminScreen(viewModel = productViewModel)
                }

                "cliente" -> {
                    // Pantalla de Cliente
                    // AHORA SÍ: Pasamos el parámetro onLogout que agregamos en TiendaApp
                    TiendaApp(
                        viewModel = productViewModel,
                        onLogout = {
                            UserRepository.logout() // Limpiamos el usuario actual en memoria
                            currentScreen = "login" // Volvemos a la pantalla de login
                        }
                    )
                }
            }
        }
    }
}