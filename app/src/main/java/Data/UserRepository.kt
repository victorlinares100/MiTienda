package Data

import Model.LoginRequest
import Model.User
import Api.RetrofitClient
import android.util.Log

object UserRepository {

    var currentUser: User? = null
        private set

    suspend fun authenticate(email: String, passwordAttempt: String): Result<User> {
        return try {
            val request = LoginRequest(email, passwordAttempt)
            val response = RetrofitClient.apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                currentUser = response.body()
                Result.success(currentUser!!)
            } else {
                Result.failure(Exception("Error: ${response.code()} - Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error login", e)
            Result.failure(e)
        }
    }

    suspend fun registrar(usuario: User): Result<String> {
        return try {
            val response = RetrofitClient.apiService.registrar(usuario)

            if (response.isSuccessful) {
                val mensajeExito = response.body()?.string() ?: "Registro exitoso"
                Result.success(mensajeExito)
            } else {
                val mensajeError = response.errorBody()?.string() ?: "Error desconocido en el registro"
                Result.failure(Exception(mensajeError))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error registro", e)
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = RetrofitClient.apiService.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar usuarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getUsers", e)
            Result.failure(e)
        }
    }

    fun logout() {
        currentUser = null
    }
}