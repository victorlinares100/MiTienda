package com.example.mitienda

import Data.ProductRepository
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// NUEVO: Define el estado de la UI (combina la lista de DB y el carrito en memoria)
data class ProductUiState(
    val productList: List<Product> = emptyList()
)

// CAMBIO: El ViewModel ahora recibe el Repository en su constructor
class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    // 1. PRODUCTS: Se reemplaza la lista en memoria por un StateFlow que observa la DB
    val uiState: StateFlow<ProductUiState> = repository.getAllProductsStream()
        .map { ProductUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProductUiState()
        )

    // 2. CART: El carrito se mantiene en memoria para transacciones temporales
    private val _cart = mutableStateListOf<Product>()
    val cart: List<Product> get() = _cart

    // 3. addProduct: Ahora es suspend y usa el Repository
    fun addProduct(name: String, price: Double, category: ProductCategory) {
        // Ejecuta la inserción en una coroutine
        viewModelScope.launch {
            val newProduct = Product(
                name = name,
                price = price,
                category = category
            )
            repository.insertProduct(newProduct)
        }
    }

    // 4. Funciones del carrito (se mantienen igual)
    fun addToCart(product: Product) {
        _cart.add(product)
    }

    // NUEVO: La función de filtrado ahora usa la lista del StateFlow
    fun getProductsByCategory(category: ProductCategory): List<Product> {
        return uiState.value.productList.filter { it.category == category }
    }

    // clearAll (No es necesario borrar el DB, se puede quitar o reescribir para borrar la DB si es necesario)
    // fun clearAll() { ... }
}