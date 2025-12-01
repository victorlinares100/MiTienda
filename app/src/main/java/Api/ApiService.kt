package Api

import Model.LoginRequest
import Model.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Login: Enviamos LoginRequest, recibimos User
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    // Registro: Enviamos User, recibimos texto plano (ResponseBody)
    @POST("api/v1/auth/registro")
    suspend fun registrar(@Body user: User): Response<ResponseBody>
}