package ViewModel

import Data.UserRepository
import Model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    // --- ESTADOS DE LA UI ---
    // ¿Estamos cargando? (Para mostrar la ruedita de carga)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ¿Hubo error? (Para mostrar texto rojo)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // ¿Salió todo bien? (Para mostrar texto verde o navegar)
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    // --- FUNCIÓN REGISTRAR ---
    fun registrar(
        nombre: String,
        correo: String,
        pass: String,
        confirmPass: String,
        region: String,
        comuna: String
    ) {
        // 1. Validaciones locales antes de molestar al servidor
        if (nombre.isBlank() || correo.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Por favor completa los campos obligatorios."
            return
        }

        if (pass != confirmPass) {
            _errorMessage.value = "Las contraseñas no coinciden."
            return
        }

        // Lanzamos la corrutina (Hilo secundario)
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Limpiamos errores previos
            _successMessage.value = null

            // 2. Crear el objeto Usuario para enviar
            // Nota: El backend asigna el ID y el Rol, nosotros enviamos el resto.
            val nuevoUsuario = User(
                nombre = nombre.trim(),
                correo = correo.trim(),
                // Asegúrate de que en tu modelo User tengas el campo que corresponda al email.
                // Usualmente en tu backend Java es 'correo'.
                // Si tu modelo User pide 'email' y 'correo', llena ambos por seguridad o ajusta tu modelo.
                email = correo.trim(),
                contrasena = pass,
                region = region,
                comuna = comuna,
                rolString = "Cliente" // Forzamos que sea Cliente desde la app
            )

            // 3. Llamar al repositorio
            val resultado = UserRepository.registrar(nuevoUsuario)

            _isLoading.value = false

            // 4. Manejar la respuesta
            resultado.onSuccess { mensajeDelBackend ->
                _successMessage.value = mensajeDelBackend
            }
            resultado.onFailure { error ->
                _errorMessage.value = error.message ?: "Ocurrió un error al registrarse."
            }
        }
    }
}