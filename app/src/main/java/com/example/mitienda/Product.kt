package com.example.mitienda

import androidx.room.Entity
import androidx.room.PrimaryKey

// NUEVO: Definición de las categorías
enum class ProductCategory {
    INVIERNO,
    VERANO,
    GIMNASIO,
    OTRO
}

// CAMBIO: Se añade @Entity y @PrimaryKey
@Entity(tableName = "producto")
data class Product(
    // CAMBIO: Se añade PrimaryKey con autoGenerate para que Room maneje las IDs
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Se inicializa a 0 para que Room lo autogenere
    val name: String,
    val price: Double,
    val description: String,
    val category: ProductCategory
)