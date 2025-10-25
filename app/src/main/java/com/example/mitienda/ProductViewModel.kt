package com.example.mitienda


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ProductViewModel : ViewModel() {
    private val _products = mutableStateListOf<Product>()
    val products: List<Product> get() = _products

    private val _cart = mutableStateListOf<Product>()
    val cart: List<Product> get() = _cart

    private var nextId = 1

    // CAMBIO: addProduct ahora recibe la categoría
    fun addProduct(name: String, price: Double, category: ProductCategory) {
        val p = Product(nextId++, name, price, category)
        _products.add(p)
    }

    fun addToCart(product: Product) {
        _cart.add(product)
    }

    // NUEVO: Función para obtener productos por categoría (necesaria para el Cliente)
    fun getProductsByCategory(category: ProductCategory): List<Product> {
        return _products.filter { it.category == category }
    }

    fun clearAll() {
        _products.clear()
        _cart.clear()
        nextId = 1
    }
}