package ViewModel

import Data.ProductRepository
import Data.CarritoRepository
import Data.UserRepository
import Model.*
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

data class ProductUiState(
    val productList: List<Product> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val marcas: List<Marca> = emptyList(),
    val tallas: List<Talla> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

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

    // ----------------------------------------------------------------
    //  NUEVA SECCIÓN: GESTIÓN DE CATEGORÍAS
    // ----------------------------------------------------------------

    fun addCategory(nombre: String) {
        // Simulamos la creación generando un ID temporal basado en la hora actual
        val nuevaCat = Categoria(
            id = System.currentTimeMillis(),
            nombre = nombre
        )
        // Actualizamos la UI inmediatamente
        _uiState.update { currentState ->
            currentState.copy(
                categorias = currentState.categorias + nuevaCat
            )
        }
        // NOTA: Aquí deberías llamar a repository.createCategory(nombre) si quisieras guardarlo en BD real
    }

    fun deleteCategory(categoryId: Long) {
        // Filtramos la lista quitando la categoría seleccionada
        _uiState.update { currentState ->
            currentState.copy(
                categorias = currentState.categorias.filter { it.id != categoryId }
            )
        }
        // NOTA: Aquí deberías llamar a repository.deleteCategory(id) si quisieras borrarlo en BD real
    }

    // ----------------------------------------------------------------
    //  FIN SECCIÓN CATEGORÍAS
    // ----------------------------------------------------------------

    fun addToCart(product: Product) { _cart.add(product) }

    fun removeFromCart(product: Product) {
        _cart.removeIf { it.id == product.id }
    }

    fun removeOneFromCart(product: Product) {
        val indexToRemove = _cart.indexOfFirst { it.id == product.id }
        if (indexToRemove != -1) {
            _cart.removeAt(indexToRemove)
        }
    }

    fun clearCart() { _cart.clear() }

    fun performCheckout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userId = UserRepository.currentUser?.id

            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: Sesión no válida. Vuelve a iniciar sesión.") }
                return@launch
            }

            val groupedItems = _cart.groupBy { it.id }.mapValues { it.value.size }

            val itemsRequest = _cart.distinctBy { it.id }.map { uniqueProduct ->
                CarritoItemRequest(
                    productoId = uniqueProduct.id,
                    cantidad = groupedItems[uniqueProduct.id] ?: 1,
                    precioUnitario = uniqueProduct.price
                )
            }

            if (itemsRequest.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "El carrito está vacío.") }
                return@launch
            }


            val request = CarritoRequest(
                usuarioId = userId,
                metodoPagoId = 1,
                estadoId = 1,
                items = itemsRequest
            )

            val success = carritoRepository.procesarCompra(request)

            _uiState.update { it.copy(isLoading = false) }

            if (success) {
                clearCart()
                onSuccess()
            } else {
                _uiState.update { it.copy(errorMessage = "Hubo un error al procesar tu compra.") }
            }
        }
    }
}