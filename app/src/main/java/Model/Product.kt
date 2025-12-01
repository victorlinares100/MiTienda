package Model

import com.google.gson.annotations.SerializedName

// --- 1. CLASES AUXILIARES (Lo que viene dentro del producto en el GET) ---

data class Categoria(
    @SerializedName("id") val id: Long,
    @SerializedName("tipoCategoria") val nombre: String
)

data class Marca(
    @SerializedName("id") val id: Long,
    @SerializedName("nombreMarca") val nombre: String
)

data class Talla(
    @SerializedName("id") val id: Long,
    @SerializedName("tipoTalla") val nombre: String
)

data class Imagen(
    @SerializedName("id") val id: Long,
    // En tu React accedías a .url, y en Java tu entidad Imagen tiene el campo 'url'
    @SerializedName("url") val url: String
)

// --- 2. EL PRODUCTO PRINCIPAL (Para LEER/GET) ---
data class Product(
    @SerializedName("id") val id: Long,

    @SerializedName("nombreProducto") val name: String,

    @SerializedName("precioProducto") val price: Double,

    @SerializedName("stock") val stock: Int,

    // El backend puede devolver nulos si borraste la categoría/marca, por eso el '?'
    @SerializedName("categoria") val category: Categoria?,
    @SerializedName("marca") val brand: Marca?,
    @SerializedName("talla") val size: Talla?,
    @SerializedName("imagen") val image: Imagen?
)

// --- 3. DTO PARA ENVIAR DATOS (Para CREAR/POST y EDITAR/PUT) ---
// Esto reemplaza a tu ProductoDTO.java del backend
data class ProductRequest(
    @SerializedName("nombreProducto") val nombre: String,
    @SerializedName("precioProducto") val precio: Double,
    @SerializedName("stock") val stock: Int,
    @SerializedName("categoriaId") val categoriaId: Long,
    @SerializedName("marcaId") val marcaId: Long,
    @SerializedName("tallaId") val tallaId: Long,
    @SerializedName("imagenUrl") val imagenUrl: String?
)

// --- 4. MODELOS PARA IMGBB (Para subir imágenes) ---
data class ImgBBResponse(
    @SerializedName("data") val data: ImgBBData?,
    @SerializedName("success") val success: Boolean
)

data class ImgBBData(
    @SerializedName("url") val url: String
)