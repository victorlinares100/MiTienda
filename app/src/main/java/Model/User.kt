package Model

import com.google.gson.annotations.SerializedName

enum class Rol {
    ADMIN, CLIENT
}

// Clase para enviar los datos del login (JSON body)
data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasena") val contrasena: String
)

// Clase Usuario completa (mapeada a tu DB del backend)
data class User(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("correoUsuario") val email: String, // Ojo: tu backend enviaba "correo" o "correoUsuario"? Revisa el JSON. Asumo "correo" por tu código anterior.
    // Si tu backend devuelve "correo" usa @SerializedName("correo")
    @SerializedName("correo") val correo: String? = null,

    @SerializedName("rol") val rolString: String? = "Cliente", // El backend devuelve String
    @SerializedName("region") val region: String? = null,
    @SerializedName("comuna") val comuna: String? = null,

    // La contraseña es opcional porque el backend NO la devuelve al hacer login por seguridad
    @SerializedName("contrasena") val contrasena: String? = null
) {
    // Helper para convertir el String del backend a tu Enum de Kotlin
    val role: Rol
        get() = if (rolString?.uppercase() == "ADMIN") Rol.ADMIN else Rol.CLIENT
}