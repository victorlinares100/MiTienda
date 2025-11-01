package Data

import Model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productoDao: ProductoDao) {

    fun getAllProductsStream(): Flow<List<Product>> = productoDao.getAllProducts()

    suspend fun insertProduct(product: Product) = productoDao.insert(product)

    suspend fun updateProduct(product: Product) = productoDao.update(product)

    suspend fun deleteProduct(product: Product) = productoDao.delete(product)
}
