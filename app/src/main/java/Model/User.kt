package Model

enum class Rol {
    ADMIN, CLIENT
}

data class User(
    val email: String,
    val contraseña: String,
    val role: Rol
)


object UserRepository {
    private val users = listOf(
        User("admin@tienda.com", "12345", Rol.ADMIN),
        User("cliente@tienda.com", "12345", Rol.CLIENT)
    )

    fun authenticate(email: String, passwordAttempt: String): User? {
        // En un caso real, validarías la contraseña hasheada
        return users.find { it.email == email && it.contraseña == passwordAttempt }
    }
}