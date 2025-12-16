package com.example.mitienda

import Data.UserRepository
import Model.User
import ViewModel.RegisterViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(UserRepository)
        viewModel = RegisterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `registrar falla si hay campos obligatorios vacios`() {
        // PREPARAR: Datos vacíos
        val nombre = ""
        val correo = ""
        val pass = "123"
        val confirm = "123"

        viewModel.registrar(nombre, correo, pass, confirm, "RM", "Santiago")

        assertEquals("Por favor completa los campos obligatorios.", viewModel.errorMessage.value)

        coVerify(exactly = 0) { UserRepository.registrar(any()) }
    }

    @Test
    fun `registrar falla si las contraseñas no coinciden`() {
        val pass = "123456"
        val confirm = "654321"
        viewModel.registrar("Juan", "juan@test.com", pass, confirm, "RM", "Centro")

        assertEquals("Las contraseñas no coinciden.", viewModel.errorMessage.value)

        coVerify(exactly = 0) { UserRepository.registrar(any()) }
    }

    @Test
    fun `registrar exitoso muestra mensaje de exito y limpia errores`() = runTest {
        val mensajeExitoBackend = "Usuario registrado correctamente"

        coEvery { UserRepository.registrar(any()) } returns Result.success(mensajeExitoBackend)

        viewModel.registrar(
            nombre = "Nuevo User",
            correo = "nuevo@test.com",
            pass = "123456",
            confirmPass = "123456",
            region = "Valparaíso",
            comuna = "Viña"
        )

        assertFalse(viewModel.isLoading.value)
        assertEquals(mensajeExitoBackend, viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)
    }


    @Test
    fun `registrar fallido muestra mensaje de error del backend`() = runTest {
        val mensajeErrorBackend = "El correo ya está registrado"

        coEvery { UserRepository.registrar(any()) } returns Result.failure(Exception(mensajeErrorBackend))

        viewModel.registrar(
            nombre = "User Existente",
            correo = "existe@test.com",
            pass = "123456",
            confirmPass = "123456",
            region = "BioBio",
            comuna = "Conce"
        )

        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value)
        assertEquals(mensajeErrorBackend, viewModel.errorMessage.value)
    }
}