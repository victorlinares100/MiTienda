package ViewModel

import Data.ProductRepository
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import Model.Product
import Model.ProductCategory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProductUiState(
    val productList: List<Product> = emptyList()
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val uiState: StateFlow<ProductUiState> = repository.getAllProductsStream()
        .map { ProductUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProductUiState()
        )

    private val _cart = mutableStateListOf<Product>()
    val cart: List<Product> get() = _cart

    // --- CRUD ---
    fun addProduct(name: String, price: Double, description: String, category: ProductCategory) {
        viewModelScope.launch {
            val newProduct = Product(
                name = name,
                price = price,
                description = description,
                category = category
            )
            repository.insertProduct(newProduct)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { repository.updateProduct(product) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }


    // --- Carrito ---
    fun addToCart(product: Product) {
        _cart.add(product)
    }

    fun removeFromCart(product: Product) {
        _cart.remove(product)
    }


    fun getProductsGroupedByCategory(): Map<ProductCategory, List<Product>> {
        return uiState.value.productList.groupBy { it.category }
    }
}
