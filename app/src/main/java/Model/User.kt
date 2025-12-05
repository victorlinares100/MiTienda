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
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("correoUsuario") val email: String,
    @SerializedName("correo") val correo: String? = null,

    @SerializedName("rol") val rolString: String? = "Cliente", // El backend devuelve String
    @SerializedName("region") val region: String? = null,
    @SerializedName("comuna") val comuna: String? = null,

    @SerializedName("contrasena") val contrasena: String? = null
) {
    val role: Rol
        get() = if (rolString?.uppercase() == "ADMIN") Rol.ADMIN else Rol.CLIENT
}