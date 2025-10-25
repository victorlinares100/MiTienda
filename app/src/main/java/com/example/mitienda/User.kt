package com.example.mitienda

enum class Rol {
    ADMIN, CLIENT
}

data class User(
    val email: String,
    val contraseña: String, // En un proyecto real, nunca guardarías la contraseña en texto plano. Usaremos hash simulado.
    val role: Rol
)

// Clase de simulación de "Base de Datos" de usuarios
object UserRepository {
    // Definir un administrador y un cliente de prueba
    private val users = listOf(
        User("admin@tienda.com", "12345", Rol.ADMIN),
        User("cliente@tienda.com", "12345", Rol.CLIENT)
    )

    fun authenticate(email: String, passwordAttempt: String): User? {
        // En un caso real, validarías la contraseña hasheada
        return users.find { it.email == email && it.contraseña == passwordAttempt }
    }
}