package Model

import com.google.gson.annotations.SerializedName

enum class Rol {
    ADMIN, CLIENT
}

data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

data class User(
    @SerializedName("id") val id: Long? = null,

    // Ponemos valor por defecto "" para que nunca sea nulo al crearlo
    @SerializedName("nombre") val nombre: String? = "",

    // IMPORTANTE: Cambiamos a String? = null para evitar el crash si el JSON no trae "correoUsuario"
    @SerializedName("correoUsuario") val email: String? = null,

    // A veces el backend manda "correo" en vez de "correoUsuario", esto cubre ambos casos
    @SerializedName("correo") val correoAlternativo: String? = null,

    @SerializedName("rol") val rolString: String? = "Cliente",
    @SerializedName("region") val region: String? = null,
    @SerializedName("comuna") val comuna: String? = null,

    @SerializedName("contrasena") val contrasena: String? = null
) {
    // Lógica para obtener el rol de forma segura
    val role: Rol
        get() = if (rolString?.uppercase() == "ADMIN") Rol.ADMIN else Rol.CLIENT

    // Helper para obtener el email real (ya sea que venga en 'correoUsuario' o 'correo')
    fun getEmailSeguro(): String {
        return email ?: correoAlternativo ?: "Sin correo"
    }

    // Helper para el nombre seguro
    fun getNombreSeguro(): String {
        return nombre ?: "Sin Nombre"
    }
}