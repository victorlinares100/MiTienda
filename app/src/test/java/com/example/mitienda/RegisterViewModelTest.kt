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
        // 1. Configuramos el entorno de corrutinas
        Dispatchers.setMain(testDispatcher)

        // 2. Mockeamos el UserRepository (porque es un object)
        mockkObject(UserRepository)

        // 3. Inicializamos el ViewModel
        viewModel = RegisterViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // --- TEST 1: Validaciones básicas (No requiere Internet/Repo) ---

    @Test
    fun `registrar falla si hay campos obligatorios vacios`() {
        // PREPARAR: Datos vacíos
        val nombre = ""
        val correo = ""
        val pass = "123"
        val confirm = "123"

        // ACTUAR
        viewModel.registrar(nombre, correo, pass, confirm, "RM", "Santiago")

        // VERIFICAR
        assertEquals("Por favor completa los campos obligatorios.", viewModel.errorMessage.value)

        // Verificamos que NUNCA se llamó al repositorio (ahorro de recursos)
        coVerify(exactly = 0) { UserRepository.registrar(any()) }
    }

    @Test
    fun `registrar falla si las contraseñas no coinciden`() {
        // PREPARAR: Contraseñas distintas
        val pass = "123456"
        val confirm = "654321" // Diferente

        // ACTUAR
        viewModel.registrar("Juan", "juan@test.com", pass, confirm, "RM", "Centro")

        // VERIFICAR
        assertEquals("Las contraseñas no coinciden.", viewModel.errorMessage.value)

        // Verificamos que NUNCA se llamó al repositorio
        coVerify(exactly = 0) { UserRepository.registrar(any()) }
    }

    // --- TEST 2: Registro Exitoso ---

    @Test
    fun `registrar exitoso muestra mensaje de exito y limpia errores`() = runTest {
        // PREPARAR
        val mensajeExitoBackend = "Usuario registrado correctamente"

        // Entrenamos al Mock: Cuando llamen a registrar, devuelve Éxito
        coEvery { UserRepository.registrar(any()) } returns Result.success(mensajeExitoBackend)

        // ACTUAR
        viewModel.registrar(
            nombre = "Nuevo User",
            correo = "nuevo@test.com",
            pass = "123456",
            confirmPass = "123456",
            region = "Valparaíso",
            comuna = "Viña"
        )

        // VERIFICAR
        // 1. Ya no debe estar cargando
        assertFalse(viewModel.isLoading.value)
        // 2. El mensaje de éxito debe ser el que mandó el backend
        assertEquals(mensajeExitoBackend, viewModel.successMessage.value)
        // 3. No debe haber mensajes de error
        assertNull(viewModel.errorMessage.value)
    }

    // --- TEST 3: Registro Fallido (Error del Backend) ---

    @Test
    fun `registrar fallido muestra mensaje de error del backend`() = runTest {
        // PREPARAR
        val mensajeErrorBackend = "El correo ya está registrado"

        // Entrenamos al Mock: Cuando llamen a registrar, devuelve Falla
        coEvery { UserRepository.registrar(any()) } returns Result.failure(Exception(mensajeErrorBackend))

        // ACTUAR
        viewModel.registrar(
            nombre = "User Existente",
            correo = "existe@test.com",
            pass = "123456",
            confirmPass = "123456",
            region = "BioBio",
            comuna = "Conce"
        )

        // VERIFICAR
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.successMessage.value) // No debe haber éxito
        assertEquals(mensajeErrorBackend, viewModel.errorMessage.value) // Debe mostrar el error
    }
}