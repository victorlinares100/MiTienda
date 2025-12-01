package Data

import Model.LoginRequest
import Model.User
import Api.RetrofitClient
import android.util.Log

object UserRepository {

    // Variable para guardar el usuario logueado en memoria (sesión)
    var currentUser: User? = null
        private set

    // --- LOGIN ---
    suspend fun authenticate(email: String, passwordAttempt: String): Result<User> {
        return try {
            val request = LoginRequest(email, passwordAttempt)
            val response = RetrofitClient.apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                currentUser = response.body()
                Result.success(currentUser!!)
            } else {
                // Si falla (ej: 401 Credenciales invalidas)
                Result.failure(Exception("Error: ${response.code()} - Credenciales inválidas"))
            }
        } catch (e: Exception) {
            // Error de red (sin internet, servidor caído)
            Log.e("UserRepository", "Error login", e)
            Result.failure(e)
        }
    }

    // --- REGISTRO (NUEVO) ---
    suspend fun registrar(usuario: User): Result<String> {
        return try {
            // Llamamos al endpoint de registro
            val response = RetrofitClient.apiService.registrar(usuario)

            if (response.isSuccessful) {
                // Tu backend devuelve texto plano (ej: "Usuario registrado exitosamente").
                // response.body()?.string() lee ese texto.
                val mensajeExito = response.body()?.string() ?: "Registro exitoso"
                Result.success(mensajeExito)
            } else {
                // Si falla (ej: 400 "El correo ya está en uso"), leemos el error.
                val mensajeError = response.errorBody()?.string() ?: "Error desconocido en el registro"
                Result.failure(Exception(mensajeError))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error registro", e)
            Result.failure(e)
        }
    }

    // --- LOGOUT ---
    fun logout() {
        currentUser = null
    }
}