package Data

import Api.RetrofitClient
import Model.*
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProductRepository {

    private val api = RetrofitClient.apiService

    private val imgBBKey = "d84d25b4cf23d4403e33c8a450a58508"

    suspend fun getAllProducts(): List<Product> {
        return try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("Repo", "Error al obtener productos: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("Repo", "Error de red: ${e.message}")
            emptyList()
        }
    }

    suspend fun insertProduct(request: ProductRequest): Boolean {
        return try {
            val response = api.createProduct(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("Repo", "Error al crear: ${e.message}")
            false
        }
    }

    suspend fun updateProduct(id: Long, request: ProductRequest): Boolean {
        return try {
            val response = api.updateProduct(id, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("Repo", "Error al actualizar: ${e.message}")
            false
        }
    }

    suspend fun deleteProduct(id: Long): Boolean {
        return try {
            val response = api.deleteProduct(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("Repo", "Error al eliminar: ${e.message}")
            false
        }
    }

    suspend fun getCategorias(): List<Categoria> = try {
        api.getCategorias().body() ?: emptyList()
    } catch (e: Exception) { emptyList() }

    suspend fun getMarcas(): List<Marca> = try {
        api.getMarcas().body() ?: emptyList()
    } catch (e: Exception) { emptyList() }

    suspend fun getTallas(): List<Talla> = try {
        api.getTallas().body() ?: emptyList()
    } catch (e: Exception) { emptyList() }


    suspend fun uploadImage(file: File): String? {
        return try {
            // Preparamos el archivo para enviarlo por internet
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            // Llamamos a la API de ImgBB
            val response = api.uploadImage(apiKey = imgBBKey, image = body)

            if (response.isSuccessful) {
                // Retornamos la URL que nos da ImgBB
                response.body()?.data?.url
            } else {
                Log.e("ImgBB", "Error subida: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ImgBB", "Exception subida", e)
            null
        }
    }
}