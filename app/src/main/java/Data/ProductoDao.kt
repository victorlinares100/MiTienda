package Data


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.mitienda.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM producto ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    // Al insertar, si ya existe una ID, la reemplaza (útil para futuras actualizaciones)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    // Agrega más consultas si las necesitas (ej: delete, update)
}