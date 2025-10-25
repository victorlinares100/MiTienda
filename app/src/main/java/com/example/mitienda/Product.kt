package com.example.mitienda

// NUEVO: Definición de las categorías
enum class ProductCategory {
    INVIERNO,
    VERANO,
    GIMNASIO,
    OTRO // Opcional: para productos que no encajan
}

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: ProductCategory
)