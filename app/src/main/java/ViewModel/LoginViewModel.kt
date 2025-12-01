package ViewModel

import Data.UserRepository
import Model.Rol
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Estado para la UI (Cargando, Error, Éxito)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Función para loguearse
    fun login(email: String, pass: String, onLoginSuccess: (Rol) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = UserRepository.authenticate(email, pass)

            _isLoading.value = false

            result.onSuccess { user ->
                // Login exitoso, notificamos a la pantalla
                onLoginSuccess(user.role)
            }
            result.onFailure { exception ->
                // Login fallido
                _errorMessage.value = exception.message ?: "Error desconocido"
            }
        }
    }
}