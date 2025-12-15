package ViewModel

import Data.ProductRepository
import Data.CarritoRepository
import Data.UserRepository
import Model.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

// 1. Agregamos la lista de usuarios al estado global
data class ProductUiState(
    val productList: List<Product> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val marcas: List<Marca> = emptyList(),
    val tallas: List<Talla> = emptyList(),
    val usuarios: List<User> = emptyList(), // <--- NUEVO CAMPO
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

    // Variables para la pantalla de Admin Usuarios
    var userSearchQuery by mutableStateOf("")
    var selectedUserForDetail by mutableStateOf<User?>(null)

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Cargamos todo en paralelo: Productos, Categorías... Y AHORA USUARIOS
                val products = repository.getAllProducts()
                val cats = repository.getCategorias()
                val brands = repository.getMarcas()
                val sizes = repository.getTallas()

                // Llamamos al repositorio de usuarios (es un object, no necesitamos inyectarlo)
                val usersResult = UserRepository.getAllUsers()
                val usersList = usersResult.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        productList = products,
                        categorias = cats,
                        marcas = brands,
                        tallas = sizes,
                        usuarios = usersList, // <--- Guardamos los usuarios reales
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

    // --- LÓGICA DE FILTRADO DE USUARIOS ---
    fun getFilteredUsers(): List<User> {
        val currentUsers = _uiState.value.usuarios // Usamos la lista real del estado
        if (userSearchQuery.isBlank()) return currentUsers

        return currentUsers.filter { user ->
            // Usamos el operador Elvis (?:)
            // Significa: "Si el nombre es nulo, usa un texto vacío"
            val nombreSeguro = user.nombre ?: ""

            // "Si el email es nulo, prueba con el correo alternativo, y si también es nulo, usa vacío"
            val emailSeguro = user.email ?: user.correoAlternativo ?: ""

            nombreSeguro.contains(userSearchQuery, ignoreCase = true) ||
                    emailSeguro.contains(userSearchQuery, ignoreCase = true)
        }
    }

    // ... (El resto de tus funciones de Productos: addProduct, updateProduct, deleteProduct, addCategory siguen igual) ...

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
            if (success) refreshData()
            else _uiState.update { it.copy(isLoading = false, errorMessage = "Error al crear producto") }
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
            if (success) refreshData()
            else _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar") }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteProduct(product.id)
            if (success) refreshData()
            else _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar") }
        }
    }

    // Categorias (Local temporal para UI)
    fun addCategory(nombre: String) {
        val nuevaCat = Categoria(id = System.currentTimeMillis(), nombre = nombre)
        _uiState.update { it.copy(categorias = it.categorias + nuevaCat) }
    }
    fun deleteCategory(categoryId: Long) {
        _uiState.update { it.copy(categorias = it.categorias.filter { it.id != categoryId }) }
    }

    // ... (Lógica del Carrito sigue igual) ...
    fun addToCart(product: Product) { _cart.add(product) }
    fun removeFromCart(product: Product) { _cart.removeIf { it.id == product.id } }
    fun removeOneFromCart(product: Product) {
        val indexToRemove = _cart.indexOfFirst { it.id == product.id }
        if (indexToRemove != -1) _cart.removeAt(indexToRemove)
    }
    fun clearCart() { _cart.clear() }

    fun performCheckout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = UserRepository.currentUser?.id
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sesión no válida") }
                return@launch
            }
            val groupedItems = _cart.groupBy { it.id }.mapValues { it.value.size }
            val itemsRequest = _cart.distinctBy { it.id }.map { uniqueProduct ->
                CarritoItemRequest(uniqueProduct.id, groupedItems[uniqueProduct.id] ?: 1, uniqueProduct.price)
            }
            if (itemsRequest.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Carrito vacío") }
                return@launch
            }
            val request = CarritoRequest(userId, 1, 1, itemsRequest)
            val success = carritoRepository.procesarCompra(request)
            _uiState.update { it.copy(isLoading = false) }
            if (success) {
                clearCart()
                onSuccess()
            } else {
                _uiState.update { it.copy(errorMessage = "Error al procesar compra") }
            }
        }
    }
}