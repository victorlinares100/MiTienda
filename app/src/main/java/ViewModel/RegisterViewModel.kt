package ViewModel

import Data.UserRepository
import Model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun registrar(
        nombre: String,
        correo: String,
        pass: String,
        confirmPass: String,
        region: String,
        comuna: String
    ) {
        if (nombre.isBlank() || correo.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Por favor completa los campos obligatorios."
            return
        }

        if (pass != confirmPass) {
            _errorMessage.value = "Las contraseñas no coinciden."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            val nuevoUsuario = User(
                nombre = nombre.trim(),
                correo = correo.trim(),
                email = correo.trim(),
                contrasena = pass,
                region = region,
                comuna = comuna,
                rolString = "Cliente"
            )

            val resultado = UserRepository.registrar(nuevoUsuario)

            _isLoading.value = false

            resultado.onSuccess { mensajeDelBackend ->
                _successMessage.value = mensajeDelBackend
            }
            resultado.onFailure { error ->
                _errorMessage.value = error.message ?: "Ocurrió un error al registrarse."
            }
        }
    }
}