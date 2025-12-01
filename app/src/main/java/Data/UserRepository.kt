package Data

import Model.LoginRequest
import Model.User
import Api.RetrofitClient
import android.util.Log

object UserRepository {

    // Variable para guardar el usuario logueado en memoria (sesión)
    var currentUser: User? = null
        private set

    // Función suspendida (asíncrona) para llamar a la API
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

    fun logout() {
        currentUser = null
    }
}