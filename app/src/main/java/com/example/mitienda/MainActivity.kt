package com.example.mitienda

import Data.AppDatabase
import Data.ProductRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import Screens.TiendaApp
class MainActivity : ComponentActivity() {

    // Inicializa el ViewModel usando el Factory.
    private val viewModel: ProductViewModel by viewModels {
        ViewModelFactory(
            ProductRepository(
                AppDatabase.getDatabase(this).productoDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Utilizamos la función del tema

                // Pasa la instancia única del ViewModel a la aplicación
                TiendaApp(viewModel = viewModel)

        }
    }
}
