package Api

import Model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- AUTENTICACIÓN ---
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("api/v1/auth/registro")
    suspend fun registrar(@Body user: User): Response<ResponseBody>

    // --- PRODUCTOS (CRUD) ---
    @GET("api/v1/productos")
    suspend fun getProducts(): Response<List<Product>>

    @POST("api/v1/productos")
    suspend fun createProduct(@Body request: ProductRequest): Response<Product>

    @PUT("api/v1/productos/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body request: ProductRequest): Response<Product>

    @DELETE("api/v1/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Void>

    // --- DROPDOWNS (Listas para el formulario) ---
    @GET("api/v1/categorias")
    suspend fun getCategorias(): Response<List<Categoria>>

    @GET("api/v1/marcas")
    suspend fun getMarcas(): Response<List<Marca>>

    @GET("api/v1/tallas")
    suspend fun getTallas(): Response<List<Talla>>

    @POST("api/v1/comprobantes/carrito")
    suspend fun procesarCompra(@Body request: CarritoRequest): Response<ComprobanteResponse>
    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String = "https://api.imgbb.com/1/upload",
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): Response<ImgBBResponse>
}