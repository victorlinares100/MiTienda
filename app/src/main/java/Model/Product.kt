package Model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProductCategory {
    INVIERNO,
    VERANO,
    GIMNASIO,
    OTRO
}

@Entity(tableName = "producto")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val description: String,
    val category: ProductCategory
)