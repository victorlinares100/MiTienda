package com.example.mitienda

import Data.CarritoRepository
import Data.ProductRepository
import Data.UserRepository
import Model.Product
import Model.User
import ViewModel.ProductViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    private val productRepository = mockk<ProductRepository>(relaxed = true)
    private val carritoRepository = mockk<CarritoRepository>(relaxed = true)
    private lateinit var viewModel: ProductViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(UserRepository)

        coEvery { productRepository.getAllProducts() } returns emptyList()
        coEvery { UserRepository.getAllUsers() } returns Result.success(emptyList())

        viewModel = ProductViewModel(productRepository, carritoRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // --- TEST 1: Carrito ---
    @Test
    fun `addToCart agrega productos correctamente`() {
        // CORREGIDO: Llenamos todos los campos obligatorios
        val producto = Product(
            id = 1,
            name = "Zapatilla",
            price = 5000.0,
            stock = 10,
            category = null, // Obligatorio pasar null
            brand = null,    // Obligatorio pasar null
            size = null,     // Obligatorio pasar null
            image = null     // Obligatorio pasar null
        )

        viewModel.addToCart(producto)

        assertEquals(1, viewModel.cart.size)
        assertEquals("Zapatilla", viewModel.cart[0].name)
    }

    @Test
    fun `clearCart deja el carrito vacio`() {
        // CORREGIDO: Rellenamos con datos dummy
        val producto = Product(
            id = 1,
            name = "Dummy",
            price = 100.0,
            stock = 10,
            category = null, brand = null, size = null, image = null
        )
        viewModel.addToCart(producto)

        viewModel.clearCart()

        assertEquals(0, viewModel.cart.size)
    }

    // --- TEST 2: Buscador de Usuarios ---
    @Test
    fun `getFilteredUsers filtra por nombre ignorando mayusculas`() = runTest {
        // Asegúrate de que tu modelo User tenga estos campos o ajusta según tu modelo
        val user1 = User(id = 1, nombre = "Juan Perez", email = "juan@test.com")
        val user2 = User(id = 2, nombre = "Maria Gomez", email = "maria@test.com")
        val listaUsuarios = listOf(user1, user2)

        coEvery { UserRepository.getAllUsers() } returns Result.success(listaUsuarios)
        viewModel.refreshData()

        viewModel.userSearchQuery = "JUAN"
        val resultado = viewModel.getFilteredUsers()

        assertEquals(1, resultado.size)
        assertEquals("Juan Perez", resultado[0].nombre)
    }

    @Test
    fun `getFilteredUsers no falla con usuarios con nombre Nulo`() = runTest {
        val userNormal = User(id = 1, nombre = "Ana", email = "ana@test.com")
        // Ojo: Si tu User requiere todos los campos, rellénalos aquí también
        val userNulo = User(id = 2, nombre = null, email = "nulo@test.com")

        coEvery { UserRepository.getAllUsers() } returns Result.success(listOf(userNormal, userNulo))
        viewModel.refreshData()

        viewModel.userSearchQuery = "Ana"
        val resultado = viewModel.getFilteredUsers()

        assertEquals(1, resultado.size)
        assertEquals("Ana", resultado[0].nombre)
    }

    // --- TEST 3: Compra ---
    @Test
    fun `performCheckout exitoso vacia el carrito`() = runTest {
        val usuarioLogueado = User(id = 100, nombre = "Cliente", email = "c@c.com")
        every { UserRepository.currentUser } returns usuarioLogueado

        coEvery { carritoRepository.procesarCompra(any()) } returns true

        // CORREGIDO: Producto completo
        val prodParaComprar = Product(
            id = 1,
            name = "Item",
            price = 1000.0,
            stock = 5,
            category = null, brand = null, size = null, image = null
        )
        viewModel.addToCart(prodParaComprar)

        var compraExitosa = false
        viewModel.performCheckout {
            compraExitosa = true
        }

        assertTrue("El callback de éxito debió ejecutarse", compraExitosa)
        assertEquals(0, viewModel.cart.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `performCheckout falla si no hay usuario logueado`() = runTest {
        every { UserRepository.currentUser } returns null

        // CORREGIDO: Producto completo
        val prod = Product(
            id = 1,
            name = "Item",
            price = 100.0,
            stock = 5,
            category = null, brand = null, size = null, image = null
        )
        viewModel.addToCart(prod)

        viewModel.performCheckout {
            fail("No debería tener éxito sin usuario")
        }

        assertEquals("Sesión no válida", viewModel.uiState.value.errorMessage)
    }
}