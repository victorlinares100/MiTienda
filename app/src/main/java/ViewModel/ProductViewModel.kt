package ViewModel

import Data.ProductRepository
import Data.CarritoRepository // <--- Nuevo Import
import Data.UserRepository    // <--- Nuevo Import (Para sacar el ID del usuario)
import Model.*
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

// Estado de la pantalla
data class ProductUiState(
    val productList: List<Product> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val marcas: List<Marca> = emptyList(),
    val tallas: List<Talla> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// CAMBIO AQUÍ: Ahora recibimos dos repositorios en el constructor
class ProductViewModel(
    private val repository: ProductRepository,
    private val carritoRepository: CarritoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _cart = mutableStateListOf<Product>()
    val cart: List<Product> get() = _cart

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
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

    // --- CRUD DE PRODUCTOS ---

    fun addProduct(
        name: String, price: Double, stock: Int,
        catId: Long, brandId: Long, sizeId: Long, imageFile: File?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            var imgUrl = ""
            if (imageFile != null) {
                imgUrl = repository.uploadImage(imageFile) ?: ""
            }

            val request = ProductRequest(
                nombre = name, precio = price, stock = stock,
                categoriaId = catId, marcaId = brandId, tallaId = sizeId,
                imagenUrl = imgUrl.ifBlank { null }
            )

            val success = repository.insertProduct(request)

            if (success) {
                refreshData()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al crear producto") }
            }
        }
    }

    fun updateProduct(
        id: Long, name: String, price: Double, stock: Int,
        catId: Long, brandId: Long, sizeId: Long, imageFile: File?, currentImageUrl: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            var finalImgUrl = currentImageUrl
            if (imageFile != null) {
                val newUrl = repository.uploadImage(imageFile)
                if (newUrl != null) finalImgUrl = newUrl
            }

            val request = ProductRequest(
                nombre = name, precio = price, stock = stock,
                categoriaId = catId, marcaId = brandId, tallaId = sizeId,
                imagenUrl = finalImgUrl
            )

            val success = repository.updateProduct(id, request)

            if (success) {
                refreshData()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar") }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteProduct(product.id)
            if (success) {
                refreshData()
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar") }
            }
        }
    }

    // --- CARRITO LOCAL ---
    fun addToCart(product: Product) { _cart.add(product) }
    fun removeFromCart(product: Product) { _cart.remove(product) }
    fun clearCart() { _cart.clear() }

    // --- PROCESAR COMPRA (CHECKOUT) ---
    // Esta es la nueva función que conecta con el CarritoRepository
    fun performCheckout(quantities: Map<Long, Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Obtenemos el usuario actual
            val userId = UserRepository.currentUser?.id

            Log.d("ViewModel", "Intentando pagar con Usuario ID: $userId")

            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: Sesión no válida. Vuelve a iniciar sesión.") }
                return@launch
            }

            // 2. Mapeamos los productos del carrito al formato que pide el Backend
            val itemsRequest = _cart.map { product ->
                CarritoItemRequest(
                    productoId = product.id,
                    cantidad = quantities[product.id] ?: 1,
                    precioUnitario = product.price
                )
            }

            // 3. Creamos la solicitud completa
            val request = CarritoRequest(
                usuarioId = userId,
                metodoPagoId = 1, // Por defecto 1, como en tu React
                estadoId = 1,     // Por defecto 1 (Pendiente)
                items = itemsRequest
            )

            // 4. Llamamos al nuevo repositorio de carrito
            val success = carritoRepository.procesarCompra(request)

            _uiState.update { it.copy(isLoading = false) }

            if (success) {
                clearCart() // Vaciamos el carrito local
                onSuccess() // Notificamos éxito
            } else {
                _uiState.update { it.copy(errorMessage = "Hubo un error al procesar tu compra.") }
            }
        }
    }
}