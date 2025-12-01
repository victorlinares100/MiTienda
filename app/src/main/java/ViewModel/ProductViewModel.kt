package ViewModel

import Data.ProductRepository
import Model.*
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

// Estado de la pantalla: Lista de productos + Listas para los dropdowns + Estados de carga
data class ProductUiState(
    val productList: List<Product> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val marcas: List<Marca> = emptyList(),
    val tallas: List<Talla> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    // Usamos MutableStateFlow para manejar el estado reactivo
    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    // Carrito local (se mantiene igual)
    private val _cart = mutableStateListOf<Product>()
    val cart: List<Product> get() = _cart

    // Al iniciar el ViewModel, cargamos todo
    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Usamos async para cargar todo en paralelo y ser más rápidos
                // Nota: Si no quieres complejidad, puedes llamarlos uno por uno.
                val products = repository.getAllProducts()
                val cats = repository.getCategorias()
                val brands = repository.getMarcas()
                val sizes = repository.getTallas()

                _uiState.update {
                    it.copy(
                        productList = products,
                        categorias = cats,
                        marcas = brands,
                        tallas = sizes,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}")
                }
            }
        }
    }

    // --- CRUD ---

    // 1. CREAR PRODUCTO
    fun addProduct(
        name: String,
        price: Double,
        stock: Int,
        catId: Long,
        brandId: Long,
        sizeId: Long,
        imageFile: File?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // A. Subir imagen (si existe)
            var imgUrl = ""
            if (imageFile != null) {
                // Subimos a ImgBB y obtenemos la URL
                imgUrl = repository.uploadImage(imageFile) ?: ""
            }

            // B. Crear el objeto DTO para enviar
            val request = ProductRequest(
                nombre = name,
                precio = price,
                stock = stock,
                categoriaId = catId,
                marcaId = brandId,
                tallaId = sizeId,
                imagenUrl = imgUrl.ifBlank { null } // Si falló la subida o no hay, enviamos null
            )

            // C. Enviar al Backend
            val success = repository.insertProduct(request)

            if (success) {
                refreshData() // Recargar la lista si salió bien
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al crear producto") }
            }
        }
    }

    // 2. ACTUALIZAR PRODUCTO
    fun updateProduct(
        id: Long,
        name: String,
        price: Double,
        stock: Int,
        catId: Long,
        brandId: Long,
        sizeId: Long,
        imageFile: File?,
        currentImageUrl: String? // URL que ya tenía el producto (por si no subimos una nueva)
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // A. Ver si subimos imagen nueva o usamos la vieja
            var finalImgUrl = currentImageUrl
            if (imageFile != null) {
                val newUrl = repository.uploadImage(imageFile)
                if (newUrl != null) finalImgUrl = newUrl
            }

            // B. Crear DTO
            val request = ProductRequest(
                nombre = name,
                precio = price,
                stock = stock,
                categoriaId = catId,
                marcaId = brandId,
                tallaId = sizeId,
                imagenUrl = finalImgUrl
            )

            // C. Actualizar
            val success = repository.updateProduct(id, request)

            if (success) {
                refreshData()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar") }
            }
        }
    }

    // 3. ELIMINAR PRODUCTO
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteProduct(product.id) // Usamos el ID correcto
            if (success) {
                refreshData()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar") }
            }
        }
    }

    // --- Carrito (Memoria Local) ---
    fun addToCart(product: Product) {
        _cart.add(product)
    }

    fun removeFromCart(product: Product) {
        _cart.remove(product)
    }

    fun clearCart() {
        _cart.clear()
    }
}
