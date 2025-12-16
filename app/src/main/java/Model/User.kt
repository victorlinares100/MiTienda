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

    @SerializedName("nombre") val nombre: String? = "",

    @SerializedName("correoUsuario") val email: String? = null,

    @SerializedName("correo") val correoAlternativo: String? = null,

    @SerializedName("rol") val rolString: String? = "Cliente",
    @SerializedName("region") val region: String? = null,
    @SerializedName("comuna") val comuna: String? = null,

    @SerializedName("contrasena") val contrasena: String? = null
) {
    val role: Rol
        get() = if (rolString?.uppercase() == "ADMIN") Rol.ADMIN else Rol.CLIENT

    fun getEmailSeguro(): String {
        return email ?: correoAlternativo ?: "Sin correo"
    }

    fun getNombreSeguro(): String {
        return nombre ?: "Sin Nombre"
    }
}