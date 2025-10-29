package Data



import com.example.mitienda.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productoDao: ProductoDao) {

    fun getAllProductsStream(): Flow<List<Product>> = productoDao.getAllProducts()

    suspend fun insertProduct(product: Product) = productoDao.insert(product)
}