package ViewModel

import Data.ProductRepository
import Data.CarritoRepository // <--- Importamos el repo del carrito
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val productRepo: ProductRepository,
    private val carritoRepo: CarritoRepository // <--- Nuevo parámetro
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Inyectamos ambos repositorios al ViewModel
            return ProductViewModel(productRepo, carritoRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}