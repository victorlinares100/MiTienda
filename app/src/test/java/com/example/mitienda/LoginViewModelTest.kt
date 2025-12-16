package com.example.mitienda

import Data.UserRepository
import Model.Rol
import Model.User
import ViewModel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import io.mockk.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(UserRepository)
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `login exitoso debe cambiar isLoading y ejecutar callback`() = runTest {
        val email = "test@correo.com"
        val pass = "123456"
        val usuarioFalso = User(
            id = 1L,
            nombre = "Test",
            email = email,
            rolString = "Cliente"
        )
        coEvery { UserRepository.authenticate(email, pass) } returns Result.success(usuarioFalso)

        var loginExitoso = false

        viewModel.login(email, pass) { rol ->
            loginExitoso = true
            assertEquals(Rol.CLIENT, rol)
        }

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
        assertTrue("El callback de éxito debería haberse ejecutado", loginExitoso)
    }

    @Test
    fun `login fallido debe actualizar errorMessage`() = runTest {
        val email = "error@correo.com"
        val pass = "malapass"
        val mensajeError = "Credenciales inválidas"

        coEvery { UserRepository.authenticate(email, pass) } returns Result.failure(Exception(mensajeError))

        viewModel.login(email, pass) {
            fail("No debería entrar al success si el login falla")
        }

        assertFalse(viewModel.isLoading.value)
        assertEquals(mensajeError, viewModel.errorMessage.value)
    }
}