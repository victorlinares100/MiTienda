package ViewModel

import Data.UserRepository
import Model.Rol
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, pass: String, onLoginSuccess: (Rol) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = UserRepository.authenticate(email, pass)

            _isLoading.value = false

            result.onSuccess { user ->
                onLoginSuccess(user.role)
            }
            result.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Error desconocido"
            }
        }
    }
}