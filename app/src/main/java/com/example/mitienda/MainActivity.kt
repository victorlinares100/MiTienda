package com.example.mitienda

import Data.AppDatabase
import Data.ProductRepository
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import Screens.TiendaApp
import ViewModel.ProductViewModel
import ViewModel.ViewModelFactory

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
            TiendaApp(viewModel = viewModel)
        }
    }
}
