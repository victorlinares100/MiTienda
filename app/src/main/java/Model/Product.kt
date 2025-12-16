package Model

import com.google.gson.annotations.SerializedName


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
    @SerializedName("url") val url: String
)

data class Product(
    @SerializedName("id") val id: Long,

    @SerializedName("nombreProducto") val name: String,

    @SerializedName("precioProducto") val price: Double,

    @SerializedName("stock") val stock: Int,

    @SerializedName("categoria") val category: Categoria?,
    @SerializedName("marca") val brand: Marca?,
    @SerializedName("talla") val size: Talla?,
    @SerializedName("imagen") val image: Imagen?
)

data class ProductRequest(
    @SerializedName("nombreProducto") val nombre: String,
    @SerializedName("precioProducto") val precio: Double,
    @SerializedName("stock") val stock: Int,
    @SerializedName("categoriaId") val categoriaId: Long,
    @SerializedName("marcaId") val marcaId: Long,
    @SerializedName("tallaId") val tallaId: Long,
    @SerializedName("imagenUrl") val imagenUrl: String?
)

data class ImgBBResponse(
    @SerializedName("data") val data: ImgBBData?,
    @SerializedName("success") val success: Boolean
)

data class ImgBBData(
    @SerializedName("url") val url: String
)